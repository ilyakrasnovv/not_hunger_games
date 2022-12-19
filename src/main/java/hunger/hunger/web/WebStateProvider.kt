package hunger.hunger.web

import hunger.hunger.Hunger
import hunger.hunger.models.GameStatePlayer
import hunger.hunger.models.StateProvider
import io.ktor.client.call.*
import io.ktor.client.request.*

class WebStateProvider : StateProvider {
    override suspend fun players(): List<GameStatePlayer> {
        val res: List<GameStatePlayer> = Hunger.client.get("${Hunger.hConfig.LOGIC_SERVER_URL}/game_state/players") {
            parameter("token", Hunger.hConfig.LOGIC_SERVER_ACCESS_TOKEN)
        }.body()
        return res
    }
}