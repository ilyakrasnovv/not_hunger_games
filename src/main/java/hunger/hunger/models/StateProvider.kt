package hunger.hunger.models

interface StateProvider {
    fun players(): List<GameStatePlayer>
}