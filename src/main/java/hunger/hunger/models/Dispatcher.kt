package hunger.hunger.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

abstract class Dispatcher {
    protected abstract fun sendData(content: String)

    private inline fun <reified T> contentSender(type: String, block: () -> (T & Any)) {
        val content = Content(type, System.currentTimeMillis(), block())
        sendData(Json.encodeToString(content))
    }

    @Serializable
    protected data class Content<T>(
        val type: String,
        val timestamp: Long,
        val data: T & Any,
    )

    @Serializable
    data class CaptureData(
        val winner: String,
        val looser: String,
    )

    fun capture(winner: Player, looser: OfflinePlayer) = contentSender("capture") {
        CaptureData(winner.name, looser.name!!)
    }

    @Serializable
    data class EggCockData(
        val owner: String,
    )

    fun eggCock(owner: Player) = contentSender("eggcock") {
        EggCockData(owner.name)
    }
}
