package hunger.hunger.utilities

import hunger.hunger.Hunger
import org.bukkit.metadata.MetadataValue
import org.bukkit.metadata.Metadatable

fun Metadatable.getPluginMetadata(metadataKey: String) : MetadataValue? {
    return getMetadata(metadataKey).find {
        it.owningPlugin == Hunger.instance
    }
}