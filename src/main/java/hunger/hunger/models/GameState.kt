package hunger.hunger.models

import com.lkeehl.tagapi.TagAPI
import com.lkeehl.tagapi.TagBuilder
import hunger.hunger.Hunger
import hunger.hunger.dataManaging.ALLOWED_DISTANCE_TO_BASE
import hunger.hunger.utilities.generateHWorld
import kotlinx.coroutines.runBlocking
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import java.io.File

class GameState(private val provider: StateProvider, val dispatcher: Dispatcher) {
    companion object {
        private val worldNameFile = File(Hunger.instance.dataFolder, "current_world_name.txt")
    }

    var players = listOf<GameStatePlayer>()
    lateinit var world: World
    private var shortnames = mutableMapOf<String, String>()

    init {
        update {
            synchronized(worldNameFile) {
                worldNameFile.createNewFile()
                val worldName = worldNameFile.readText().trim()
                world = if (worldName.isEmpty())
                    Hunger.instance.server.worlds.first()
                else
                    Hunger.instance.server.createWorld(WorldCreator(worldName))!!
            }
        }
    }

    fun <T> update(block: () -> T): T {
        runBlocking {
            players = provider.players()
        }
        val returnValue = block()
        players.filter { it.player.isOnline }.forEach { gameStatePlayer ->
            val player = gameStatePlayer.player as Player
            TagAPI.getTag(player) ?: TagAPI.removeTag(player)
            TagBuilder.create(player).generateLabel(gameStatePlayer).build().giveTag()
        }
        shortnames.clear()
        players.forEach { shortnames[it.userName] = it.shortCode }
        return returnValue
    }

    private val colors = ChatColor.values().toMutableList().filter {
        it !in listOf(
            ChatColor.RESET,
            ChatColor.MAGIC,
            ChatColor.BOLD,
            ChatColor.ITALIC,
            ChatColor.STRIKETHROUGH,
            ChatColor.UNDERLINE,
            ChatColor.GRAY,
            ChatColor.BLACK,
        )
    }

    private fun TagBuilder.generateLabel(gameStatePlayer: GameStatePlayer): TagBuilder {
        fun calculateColor(name: String) = colors[name.hashCode() % colors.size]
        if (gameStatePlayer.admin) {
            withLine { "" + ChatColor.WHITE + ChatColor.BOLD + "админ" }
                .withLine { "" + ChatColor.BLACK + ChatColor.BOLD + gameStatePlayer.userName }
        } else {
            withLine {
                "" +
                        calculateColor(gameStatePlayer.leader) +
                        gameStatePlayer.userName +
                        " " + ChatColor.UNDERLINE +
                        "[${
                            shortnames[gameStatePlayer.leader]
                        }${
                            if (gameStatePlayer.leader == gameStatePlayer.userName) " ♛" else ""
                        }]"
            }
        }
        return this
    }

    fun ratedPlayersAmount(): Int =
        players.count { !it.admin }

    fun ratedPlayers(): List<GameStatePlayer> =
        players.filter { !it.admin }

    fun getGameStatePlayer(player: Player): GameStatePlayer? =
        players.find { it.player.uniqueId == player.uniqueId }

    fun validatePlayer(player: Player): Boolean =
        getGameStatePlayer(player) != null

    fun isRatedPlayer(player: Player): Boolean =
        getGameStatePlayer(player)?.admin == false

    fun validateBase(player: Player, baseLocation: Location): Boolean =
        getGameStatePlayer(player)!!.let { gameStatePlayer ->
            ((baseLocation.distance(getGameStatePlayer(player)!!.initialSpawnLocation!!) <= ALLOWED_DISTANCE_TO_BASE) &&
                    (gameStatePlayer.baseData?.world != baseLocation.world))
        }

    fun placeBase(player: Player, baseLocation: Location) {
        getGameStatePlayer(player)!!.baseData = baseLocation
    }

    fun startNewGame(): Boolean = synchronized(worldNameFile) {
        update {
            val (newWorld, spawnPositions) = generateHWorld(ratedPlayersAmount())
            worldNameFile.writeText(newWorld.name)
            ratedPlayers().zip(spawnPositions) { player, spawnPosition ->
                player.initialSpawnLocation = spawnPosition
                (player.player.takeIf { it.isOnline } as Player?)?.health = 0.0
            }
            world = newWorld
            return@update true
        }
    }
}

