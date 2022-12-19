package hunger.hunger.models

import com.lkeehl.tagapi.TagAPI
import com.lkeehl.tagapi.TagBuilder
import hunger.hunger.Hunger
import hunger.hunger.utilities.generateHWorld
import hunger.hunger.utilities.setPlayerName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.util.logging.Level

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
            while (true) {
                try {
                    players = provider.players()
                } catch (e: Exception) {
                    Hunger.instance.logger.log(
                        Level.SEVERE,
                        "Couldn't fetch updates! Trying again in 1 second. Reason:\n${e.stackTraceToString()}"
                    )
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000)
                    }
                    continue
                }
                break
            }
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

    fun getGameStatePlayer(player: OfflinePlayer): GameStatePlayer? =
        players.find { it.player.uniqueId == player.uniqueId }

    fun getGameStatePlayer(userName: String): GameStatePlayer? =
        players.find { it.userName == userName }

    fun validatePlayer(player: Player): Boolean =
        getGameStatePlayer(player) != null

    fun isRatedPlayer(player: Player): Boolean =
        getGameStatePlayer(player)?.admin == false

    fun validateBase(player: Player, baseLocation: Location): Boolean =
        getGameStatePlayer(player)!!.let { gameStatePlayer ->
            ((baseLocation.distance(getGameStatePlayer(player)!!.initialSpawnLocation!!) <= Hunger.hConfig.ALLOWED_DISTANCE_TO_BASE) &&
                    (gameStatePlayer.baseData?.world != baseLocation.world))
        }

    fun placeBase(player: OfflinePlayer, baseLocation: Location) {
        val gameStatePlayer = getGameStatePlayer(player)!!
        gameStatePlayer.baseData = baseLocation
        baseLocation.block.setPlayerName(gameStatePlayer.userName)
    }

    fun startNewGame() = synchronized(worldNameFile) {
        update {
            val (newWorld, spawnPositions) = generateHWorld(ratedPlayersAmount())
            worldNameFile.writeText(newWorld.name)
            ratedPlayers().zip(spawnPositions) { player, spawnPosition ->
                player.initialSpawnLocation = spawnPosition
                (player.player.takeIf { it.isOnline } as Player?)?.health = 0.0
            }
            world = newWorld
        }
    }

    fun capture(task: Executor.Companion.CaptureTask) {
        object : BukkitRunnable() {
            override fun run() {
                val player = getGameStatePlayer(task.userName)!!
                val base = player.baseData!!
                base.block.type = Hunger.hConfig.CAPTURED_BASE_MATERIAL
                base.world.strikeLightningEffect(base)
            }
        }.runTask(Hunger.instance)
    }

    fun globalMessage(task: Executor.Companion.GlobalMessageTask) {
        players.map { it.player }.filter { it.isOnline }.map { it as Player }.forEach {
            it.showTitle(Title.title(Component.text(task.text), Component.text(task.bottomText)))
        }
    }

    fun personalMessage(task: Executor.Companion.PersonalMessageTask) {
        val player = getGameStatePlayer(task.userName)!!.player
        if (!player.isOnline)
            return
        (player as Player).sendMessage(task.text)
    }

    fun placeBases(@Suppress("UNUSED_PARAMETER") task: Executor.Companion.PlaceBasesTask) {
        object : BukkitRunnable() {
            override fun run() {
                ratedPlayers().forEach { player ->
                    if (player.baseData?.world != world) {
                        placeBase(player.player, player.initialSpawnLocation!!)
                        player.baseData = player.initialSpawnLocation
                        player.initialSpawnLocation!!.block.type = Hunger.hConfig.UNCAPTURED_BASE_MATERIAL
                    }
                }
            }
        }.runTask(Hunger.instance)
    }


    fun newGame(@Suppress("UNUSED_PARAMETER") task: Executor.Companion.NewGameTask) {
        object : BukkitRunnable() {
            override fun run() {
                startNewGame()
            }
        }.runTask(Hunger.instance)
    }
}

