package hunger.hunger.models

import hunger.hunger.Hunger
import hunger.hunger.dataManaging.ALLOWED_DISTANCE_TO_BASE
import hunger.hunger.utilities.generateHWorld
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

    init {
        update {
            synchronized(worldNameFile) {
                worldNameFile.createNewFile()
                val worldName = worldNameFile.readText().trim()
                if (worldName.isEmpty())
                    world = Hunger.instance.server.worlds.first()
                else
                    world = Hunger.instance.server.createWorld(WorldCreator(worldName))!!
            }
        }
    }

    fun <T> update(block: () -> T): T {
        players = provider.players()
        return block()
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

