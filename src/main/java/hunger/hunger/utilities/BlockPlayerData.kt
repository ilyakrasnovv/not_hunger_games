package hunger.hunger.utilities

import hunger.hunger.Hunger
import org.bukkit.block.Block

fun Block.getPlayerName(): String? =
    Hunger.blocksTiedUsers[this]

fun Block.setPlayerName(value: String) {
    Hunger.blocksTiedUsers[this] = value
}