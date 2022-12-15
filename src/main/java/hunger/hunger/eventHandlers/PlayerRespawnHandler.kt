package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawnHandler : Listener {
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val bedSpawnLocation = player.bedSpawnLocation
        val state = Hunger.state
        val initialSpawnLocation = state.getGameStatePlayer(player)?.initialSpawnLocation
        if (
            state.isRatedPlayer(event.player) &&
            bedSpawnLocation?.world != Hunger.state.world &&
            initialSpawnLocation != null
        ) {
            event.respawnLocation = initialSpawnLocation
        }
    }
}
