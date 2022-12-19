package hunger.hunger.utilities

import hunger.hunger.Hunger
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.WorldCreator

@Serializable
data class SerializableLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val pitch: Float,
    val yaw: Float,
) {
    constructor(location: Location) : this(
        location.world.name,
        location.x,
        location.y,
        location.z,
        location.pitch,
        location.yaw
    )

    fun toLocation(): Location {
        val loadedWorld =
            Hunger.instance.server.getWorld(world) ?: Hunger.instance.server.createWorld(WorldCreator(world))
        return Location(loadedWorld, x, y, z, pitch, yaw)
    }
}