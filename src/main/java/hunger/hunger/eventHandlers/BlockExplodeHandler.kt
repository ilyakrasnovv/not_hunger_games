package hunger.hunger.eventHandlers

import hunger.hunger.utilities.getPluginMetadata
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent

class BlockExplodeHandler : Listener {
    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        event.blockList().forEach { block ->
            val baseOwner = block.getPluginMetadata("base")?.value() as String?
            if (baseOwner != null)
                event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        event.blockList().forEach { block ->
            val baseOwner = block.getPluginMetadata("base")?.value() as String?
            if (baseOwner != null)
                event.isCancelled = true
        }
    }
}