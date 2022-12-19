package hunger.hunger.models

import hunger.hunger.Hunger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.OfflinePlayer

@Serializable
data class GameStatePlayer(
    @SerialName("username")
    val userName: String,
    @SerialName("shortcode")
    val shortCode: String,
    val score: Int,
    val potential: Int,
    val leader: String,
    val admin: Boolean,
) {
    val player: OfflinePlayer
        get() = Hunger.instance.server.run {
            getPlayer(userName) ?: getOfflinePlayer(userName)
        }
    var baseData: Location?
        get() = Hunger.usersLocations["base", userName]
        set(value) {
            Hunger.usersLocations["base", userName] = value!!
        }

    var initialSpawnLocation: Location?
        get() = Hunger.usersLocations["ispawn", userName]
        set(value) {
            Hunger.usersLocations["ispawn", userName] = value!!
        }
}