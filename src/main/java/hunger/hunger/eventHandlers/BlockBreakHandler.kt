package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import hunger.hunger.utilities.getPlayerName
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakHandler : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val baseOwner = event.block.getPlayerName()
        if (baseOwner != null) {
            event.isCancelled = true
            val owner = Hunger.instance.server.getOfflinePlayer(baseOwner)
            if (event.player.name != baseOwner)
                Hunger.state.dispatcher.capture(event.player, owner)
        }
    }
}