package hunger.hunger.dataManaging

import hunger.hunger.Hunger
import hunger.hunger.utilities.SerializableLocation
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Location
import java.io.File

class UserTiedLocationsManager {
    @Serializable
    private data class UsernameLocations(
        val data: MutableMap<String, SerializableLocation> = mutableMapOf()
    )

    @Serializable
    private data class Sections(
        val data: MutableMap<String, UsernameLocations> = mutableMapOf()
    )

    private val data = File(Hunger.instance.dataFolder, "${this::class.simpleName!!}_data.json")
    init {
        data.parentFile.mkdirs()
        if (data.createNewFile()) {
            data.writeText(Json.encodeToString(Sections()))
        }
    }

    operator fun get(locationType: String, name: String): Location? = synchronized(data) {
        val section = Json.decodeFromString<Sections>(data.readText()).data[locationType] ?: return@synchronized null
        val userData = section.data[name] ?: return@synchronized null
        return@synchronized userData.toLocation()
    }

    operator fun set(locationType: String, name: String, value: Location) = synchronized(data) {
        val contents = Json.decodeFromString<Sections>(data.readText())
        val section = contents.data.getOrDefault(locationType, UsernameLocations())
        section.data[name] = SerializableLocation(value)
        contents.data[locationType] = section
        data.writeText(Json.encodeToString(contents))
    }
}