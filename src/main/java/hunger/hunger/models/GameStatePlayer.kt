package hunger.hunger.models

import hunger.hunger.Hunger
import hunger.hunger.utilities.getPluginMetadata
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

data class GameStatePlayer(
    val userName: String,
    val score: Int,
    val potential: Int,
    val leader: String,
    val admin: Boolean,
) {
    val player: Player?
        get() = Hunger.instance.server.getPlayer(userName)
    var baseData: PlayerBaseData?
        get() = player?.getPluginMetadata("base") as PlayerBaseData?
        set(value) {
            player!!.setMetadata("base", FixedMetadataValue(Hunger.instance, value))
        }

     var initialSpawnLocation: Location?
         get() = player?.getPluginMetadata("ispawn") as Location?
         set(value) {
             player!!.setMetadata("ispawn", FixedMetadataValue(Hunger.instance, value))
         }
}