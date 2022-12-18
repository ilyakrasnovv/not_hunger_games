package hunger.hunger.models

interface StateProvider {
    suspend fun players(): List<GameStatePlayer>
}