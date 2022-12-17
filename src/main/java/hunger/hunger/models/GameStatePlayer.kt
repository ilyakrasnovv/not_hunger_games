package hunger.hunger.models

import hunger.hunger.Hunger
import org.bukkit.Location
import org.bukkit.OfflinePlayer

data class GameStatePlayer(
    val userName: String,
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