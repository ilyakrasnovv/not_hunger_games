package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import hunger.hunger.utilities.getPlayerName
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerInteractHandler : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (
            event.action == Action.RIGHT_CLICK_BLOCK &&
            event.clickedBlock?.getPlayerName() == event.player.name &&
            event.hand == EquipmentSlot.HAND
        )
            Hunger.state.dispatcher.eggCock(event.player)
    }
}