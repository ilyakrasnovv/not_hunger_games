package hunger.hunger.models

import org.bukkit.Location
import org.bukkit.entity.Player

class GameState(val provider: StateProvider) {
    var players = listOf<GameStatePlayer>()

    fun ratedPlayersAmount(): Int = players.count { !it.admin }
    fun playersReady(): Boolean = players.all { it.player != null }
    fun ratedPlayers(): List<Player> = players.map { it.player!! }

    fun validatePlayer(player: Player) : Boolean {
        return players.any {
            it.player == player
        }
    }

    fun registerBase() : Boolean {
        // TODO()
    }

    // TODO()
}

