package hunger.hunger.web

import hunger.hunger.Hunger
import hunger.hunger.dataManaging.LOGIC_SERVER_ACCESS_TOKEN
import hunger.hunger.dataManaging.LOGIC_SERVER_URL
import hunger.hunger.models.GameStatePlayer
import hunger.hunger.models.StateProvider
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.util.logging.Level

class WebStateProvider : StateProvider {
    override suspend fun players(): List<GameStatePlayer> {
        val res: List<GameStatePlayer> = Hunger.client.get("$LOGIC_SERVER_URL/game_state/players") {
            parameter("token", LOGIC_SERVER_ACCESS_TOKEN)
        }.body()
        Hunger.instance.logger.log(Level.INFO, res.toString())
        return res
    }
}