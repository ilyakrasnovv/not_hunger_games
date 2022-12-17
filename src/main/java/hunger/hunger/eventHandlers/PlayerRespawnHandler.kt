package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class PlayerRespawnHandler : Listener {
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val state = Hunger.state
        val initialSpawnLocation = state.getGameStatePlayer(player)?.initialSpawnLocation
        fixSpawn(event.player, event.respawnLocation, initialSpawnLocation) {
            event.respawnLocation = initialSpawnLocation!!
        }
    }

    @EventHandler
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        val player = event.player
        val state = Hunger.state
        val initialSpawnLocation = state.getGameStatePlayer(player)?.initialSpawnLocation
        fixSpawn(event.player, event.spawnLocation, initialSpawnLocation) {
            event.spawnLocation = initialSpawnLocation!!
        }
    }

    private fun fixSpawn(player: Player, spawn: Location, initialSpawnLocation: Location?, block: () -> Unit) {
        if (Hunger.state.isRatedPlayer(player) &&
            spawn.world != Hunger.state.world &&
            initialSpawnLocation != null
        )
            block()
    }
}
