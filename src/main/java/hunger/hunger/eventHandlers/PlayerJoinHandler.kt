package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinHandler : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val state = Hunger.state
        if (!state.validatePlayer(player))
            player.kick()
    }
}