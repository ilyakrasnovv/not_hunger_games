package hunger.hunger.utilities

import hunger.hunger.Hunger
import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValue

fun Player.getPluginMetadata(metadataKey: String) : MetadataValue? {
    return getMetadata(metadataKey).find {
        it.owningPlugin == Hunger.instance
    }
}