package hunger.hunger.models

import hunger.hunger.Hunger
import hunger.hunger.utilities.ALLOWED_DISTANCE_TO_BASE
import hunger.hunger.utilities.generateHWorld
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player

class GameState(private val provider: StateProvider) {
    var players = listOf<GameStatePlayer>()
    var world: World = Hunger.instance.server.worlds.first()

    init {
        update {}
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

    fun baseCapture(winner: Player, baseOwner: OfflinePlayer) {
        // TODO
    }

    fun startNewGame(): Boolean = update {
        val (newWorld, spawnPositions) = generateHWorld(ratedPlayersAmount())
        ratedPlayers().zip(spawnPositions) { player, spawnPosition ->
            player.initialSpawnLocation = spawnPosition
            (player.player.takeIf { it.isOnline } as Player?)?.health = 0.0
        }
        world = newWorld
        return@update true
    }


}

