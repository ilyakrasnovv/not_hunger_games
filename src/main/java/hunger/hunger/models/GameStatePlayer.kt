package hunger.hunger.models

import hunger.hunger.Hunger
import hunger.hunger.utilities.getPluginMetadata
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

data class GameStatePlayer(
    val userName: String,
    val score: Int,
    val potential: Int,
    val leader: String,
    val admin: Boolean,
) {
    val player: OfflinePlayer?
        get() = Hunger.instance.server.run {
            getPlayer(userName) ?: getOfflinePlayer(userName)
        }
    var baseData: PlayerBaseData?
        get() = Hunger.dataWorld["base", userName]?.value() as PlayerBaseData?
        set(value) {
            Hunger.dataWorld["base", userName] = FixedMetadataValue(Hunger.instance, value)
        }

     var initialSpawnLocation: Location?
         get() = Hunger.dataWorld["ispawn", userName]?.value() as Location?
         set(value) {
             Hunger.dataWorld["ispawn", userName] = FixedMetadataValue(Hunger.instance, value)
         }
}