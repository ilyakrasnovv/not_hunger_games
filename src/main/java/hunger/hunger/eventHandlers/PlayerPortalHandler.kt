package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerPortalHandler : Listener {
    @EventHandler
    fun onPlayerPortal(event: PlayerPortalEvent) {
        val from = event.from
        when (event.cause) {
            PlayerTeleportEvent.TeleportCause.NETHER_PORTAL -> {
                when (from.world.environment) {
                    World.Environment.NETHER -> {
                        event.to = from.multiply(8.0).apply {
                            world = Hunger.state.world
                        }
                    }

                    else -> {}
                }
            }

            PlayerTeleportEvent.TeleportCause.END_PORTAL -> {
                event.isCancelled = true
            }

            else -> {}
        }
    }
}