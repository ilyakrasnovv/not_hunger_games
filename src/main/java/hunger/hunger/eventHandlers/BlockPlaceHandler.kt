package hunger.hunger.eventHandlers

import hunger.hunger.Hunger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceHandler : Listener {
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val state = Hunger.state
        val block = event.blockPlaced
        if (!state.isRatedPlayer(player))
            return
        if (block.type != Hunger.hConfig.UNCAPTURED_BASE_MATERIAL)
            return
        if (state.validateBase(player, block.location)) {
            state.placeBase(player, block.location)
            player.showTitle(
                Title.title(
                    Component.text("База установлена!", TextColor.fromHexString("#C64B8C")),
                    Component.text("Перенести базу невозможно.", TextColor.fromHexString("#FFFF66")),
                )
            )
        }
    }
}