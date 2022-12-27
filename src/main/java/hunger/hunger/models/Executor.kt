package hunger.hunger.models

import hunger.hunger.Hunger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class Executor {
    companion object {
        @Serializable
        data class Task(val type: String, val data: JsonObject)

        @Serializable
        data class CaptureTask(
            @SerialName("username")
            val userName: String
        )

        @Serializable
        data class GlobalMessageTask(
            val text: String,
            @SerialName("bottom_text")
            val bottomText: String
        )

        @Serializable
        data class PersonalMessageTask(
            @SerialName("username")
            val userName: String, val text: String
        )

        @Serializable
        class PlaceBasesTask

        @Serializable
        class NewGameTask

        @Serializable
        data class GiveBlocksTask(
            @SerialName("username")
            val userName: String,
            @SerialName("material_type")
            val materialType: String,
            val amount: Int,
        )

        private inline fun <reified T> decode(data: JsonObject) = Json.decodeFromJsonElement<T>(data)
        fun execute(task: Task) {
            Hunger.state.run {
                when (task.type) {
                    "capture" -> capture(decode(task.data))
                    "global_message" -> globalMessage(decode(task.data))
                    "personal_message" -> personalMessage(decode(task.data))
                    "place_bases" -> placeBases(decode(task.data))
                    "new_game" -> newGame(decode(task.data))
                    "give_blocks" -> giveBlocks(decode(task.data))
                    else -> throw Throwable("Unknown data type")
                }
            }
        }
    }
}