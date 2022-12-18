package hunger.hunger.mock

import hunger.hunger.models.GameStatePlayer
import hunger.hunger.models.StateProvider

@Suppress("UNUSED")
class StateProviderHardcodeMock : StateProvider {
    override suspend fun players(): List<GameStatePlayer> {
        return listOf(
            GameStatePlayer("ilyakrasnovv", "ILKR", 0, 0, "ilyakrasnovv", true),
            GameStatePlayer("school_1507", "SC57", 0, 0, "school_1507", false),
            GameStatePlayer("noname1", "NON1", 0, 0, "noname1", false),
            GameStatePlayer("noname2", "NON2", 0, 0, "noname2", false),
            GameStatePlayer("noname3", "NON3", 0, 0, "noname3", false),
        )
    }
}