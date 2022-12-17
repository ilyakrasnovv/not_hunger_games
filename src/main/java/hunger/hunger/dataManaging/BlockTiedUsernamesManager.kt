package hunger.hunger.dataManaging

import hunger.hunger.Hunger
import hunger.hunger.utilities.SerializableLocation
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.block.Block
import java.io.File

class BlockTiedUsernamesManager {
    private val data = File(Hunger.instance.dataFolder, "${this::class.simpleName!!}_data.json")

    @Serializable
    private data class BlockData(val location: SerializableLocation, val username: String)

    init {
        data.parentFile.mkdirs()
        if (data.createNewFile()) {
            data.writeText(
                Json.encodeToString(listOf<BlockData>())
            )
        }
    }

    operator fun get(block: Block) : String? = synchronized(data) {
        val contents = Json.decodeFromString<MutableList<BlockData>>(data.readText())
        val location = SerializableLocation(block.location)
        return contents.find {
            it.location == location
        }?.username
    }

    operator fun set(block: Block, username: String) = synchronized(data) {
        val contents = Json.decodeFromString<MutableList<BlockData>>(data.readText())
        val location = SerializableLocation(block.location)
        contents.removeIf { it.location == location }
        contents.add(BlockData(location, username))
        data.writeText(Json.encodeToString(contents))
    }
}
