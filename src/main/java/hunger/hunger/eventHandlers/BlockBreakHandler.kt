package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import hunger.hunger.utilities.getPluginMetadata
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakHandler : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val baseOwner = event.block.getPluginMetadata("base")?.value() as String?
        if (baseOwner != null) {
            event.isCancelled = true
            Hunger.state.baseCapture(event.player, Hunger.instance.server.getOfflinePlayer(baseOwner))
        }
    }
}