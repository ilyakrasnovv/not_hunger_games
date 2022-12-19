package hunger.hunger.dataManaging

import hunger.hunger.Hunger
import org.bukkit.Material
import java.util.logging.Level

class Config() {
    val DISTANCE_BETWEEN_IN_CHUNKS: Int
    val ALLOWED_DISTANCE_TO_BASE: Double
    val UNCAPTURED_BASE_MATERIAL: Material
    val CAPTURED_BASE_MATERIAL: Material
    val LOGIC_SERVER_ACCESS_TOKEN: String
    val LOGIC_SERVER_URL: String
    val THIS_SERVER_ACCESS_TOKEN: String
    val PORT: Int

    val config = Hunger.instance.config

    init {
        config.run {
            addDefault("DISTANCE_BETWEEN_IN_CHUNKS", 6)
            addDefault("ALLOWED_DISTANCE_TO_BASE", 16.0)
            addDefault("UNCAPTURED_BASE_MATERIAL", "SEA_LANTERN")
            addDefault("CAPTURED_BASE_MATERIAL", "SHROOMLIGHT")
            options().copyDefaults(true)
        }
        Hunger.instance.saveConfig()
        config.run {
            try {
                DISTANCE_BETWEEN_IN_CHUNKS = getInt("DISTANCE_BETWEEN_IN_CHUNKS")
                ALLOWED_DISTANCE_TO_BASE = getDouble("ALLOWED_DISTANCE_TO_BASE")
                UNCAPTURED_BASE_MATERIAL = Material.getMaterial(getString("UNCAPTURED_BASE_MATERIAL")!!)!!
                CAPTURED_BASE_MATERIAL = Material.getMaterial(getString("CAPTURED_BASE_MATERIAL")!!)!!
                LOGIC_SERVER_ACCESS_TOKEN = getString("LOGIC_SERVER_ACCESS_TOKEN")!!
                LOGIC_SERVER_URL = getString("LOGIC_SERVER_URL")!!
                THIS_SERVER_ACCESS_TOKEN = getString("THIS_SERVER_ACCESS_TOKEN")!!
                PORT = getInt("PORT")
            } catch (e: NullPointerException) {
                Hunger.instance.logger.log(
                    Level.SEVERE,
                    "Config.yml lacks some essential data. Check that config file contains proper data and rerun the server."
                )
                throw e
            }
        }
    }
}