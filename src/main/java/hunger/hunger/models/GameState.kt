package hunger.hunger.models

import hunger.hunger.Hunger
import hunger.hunger.utilities.ALLOWED_DISTANCE_TO_BASE
import hunger.hunger.utilities.IGNORE_UNREGISTERED_PLAYERS
import hunger.hunger.utilities.generateHWorld
import org.bukkit.Location
import org.bukkit.entity.Player

class GameState(val provider: StateProvider) {
    var players = listOf<GameStatePlayer>()
    var world = Hunger.instance.server.worlds.first()

    init {
        update {}
    }

    fun <T> update(block: () -> T): T {
        players = provider.players()
        return block()
    }

    fun ratedPlayersAmount(): Int =
        players.count { !it.admin }

    fun ratedPlayersReady(): Boolean =
        players.filter { !it.admin }.all { it.player != null }

    fun ratedPlayers(): List<GameStatePlayer> =
        players.filter { !it.admin }

    fun getGameStatePlayer(player: Player): GameStatePlayer? =
        players.find { it.player == player }

    fun validatePlayer(player: Player): Boolean =
        getGameStatePlayer(player) != null

    fun isRatedPlayer(player: Player): Boolean =
        getGameStatePlayer(player)?.admin == false

    fun registerBase(player: Player, baseLocation: Location): Boolean =
        (baseLocation.distance(getGameStatePlayer(player)!!.initialSpawnLocation!!) <= ALLOWED_DISTANCE_TO_BASE)

    fun startNewGame(): Boolean = update {
        if (!ratedPlayersReady() && !IGNORE_UNREGISTERED_PLAYERS)
            return@update false
        val (newWorld, spawnPositions) = generateHWorld(ratedPlayersAmount())
        ratedPlayers().filter { it.player != null }.zip(spawnPositions) { player, spawnPosition ->
            player.initialSpawnLocation = spawnPosition
            (player.player?.takeIf { it.isOnline } as Player?)?.health = 0.0
        }
        world = newWorld
        return@update true
    }

}

