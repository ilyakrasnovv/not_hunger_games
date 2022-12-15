package hunger.hunger.mock

import hunger.hunger.models.GameStatePlayer
import hunger.hunger.models.StateProvider

class StateProviderHardcodeMock : StateProvider{
    override fun players(): List<GameStatePlayer> {
        return listOf(
            GameStatePlayer("ilyakrasnovv", 0, 0, "ilyakrasnovv", false),
            GameStatePlayer("school1507", 0, 0, "school1507", false),
            GameStatePlayer("noname1", 0, 0, "noname1", false),
            GameStatePlayer("noname2", 0, 0, "noname2", false),
            GameStatePlayer("noname3", 0, 0, "noname3", false),
        )
    }

}