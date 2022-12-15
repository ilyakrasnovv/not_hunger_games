package hunger.hunger.utilities

import hunger.hunger.Hunger
import org.bukkit.*
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

class DataWorld {
    private var dataWorld: World = Hunger.instance.server.run {
        getWorld(DATAWORLD_NAME) ?: createWorld(
            WorldCreator(
                DATAWORLD_NAME
            ).apply {
                environment(World.Environment.NORMAL)
                type(WorldType.FLAT)
            }
        )!!
    }

    private val key = "player_name"

    operator fun get(metadataKey: String, name: String,): MetadataValue? {
        val currentLocation = Location(dataWorld, 0.0, 0.0, 0.0)
        while (currentLocation.block.getPluginMetadata(key) != null) {
            val block = currentLocation.block
            val tiedUsername = block.getPluginMetadata(key)?.value() as String?
            if (name.contentEquals(tiedUsername))
                return block.getPluginMetadata(metadataKey)
            currentLocation.x += 1
        }
        return null
    }

    operator fun set(metadataKey: String, name: String, value: MetadataValue) {
        val currentLocation = Location(dataWorld, 0.0, 0.0, 0.0)
        while (true) {
            val block = currentLocation.block
            if (currentLocation.block.getPluginMetadata(key) == null) {
                currentLocation.block.type = Material.BEDROCK
                block.setMetadata(key, FixedMetadataValue(Hunger.instance, name))
            }
            val tiedUsername = block.getPluginMetadata(key)?.value() as String?
            if (name.contentEquals(tiedUsername)) {
                block.setMetadata(metadataKey, value)
                return
            }
            currentLocation.x += 1
        }
    }
}