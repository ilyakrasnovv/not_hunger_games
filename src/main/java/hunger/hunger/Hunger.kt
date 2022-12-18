package hunger.hunger

import com.lkeehl.tagapi.TagAPI
import hunger.hunger.commandExecutors.CreateHWorld
import hunger.hunger.dataManaging.BlockTiedUsernamesManager
import hunger.hunger.dataManaging.LOGIC_SERVER_ACCESS_TOKEN
import hunger.hunger.dataManaging.LOGIC_SERVER_URL
import hunger.hunger.dataManaging.UserTiedLocationsManager
import hunger.hunger.eventHandlers.*
import hunger.hunger.models.Dispatcher
import hunger.hunger.models.GameState
import hunger.hunger.web.WebStateProvider
import hunger.hunger.web.routingConfiguration
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * Not Hunger Games plugin
 * @author ilyakrasnovv
 */
class Hunger : JavaPlugin() {
    companion object {
        lateinit var instance: Hunger
        lateinit var state: GameState
        lateinit var usersLocations: UserTiedLocationsManager
        lateinit var blocksTiedUsers: BlockTiedUsernamesManager
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    override fun onEnable() {
        instance = this
        TagAPI.onEnable(this)
        state = GameState(WebStateProvider(), object : Dispatcher() {
            override fun sendData(content: String) {
                runBlocking {
                    client.post("$LOGIC_SERVER_URL/game_state/event") {
                        parameter("token", LOGIC_SERVER_ACCESS_TOKEN)
                        setBody(content)
                        contentType(ContentType.Application.Json)
                    }
                }
            }
        })
        commands = mapOf(
            "createHWorld" to CreateHWorld()
        )
        eventHandlers = listOf(
            PlayerRespawnHandler(),
            PlayerJoinHandler(),
            BlockPlaceHandler(),
            BlockExplodeHandler(),
            BlockBreakHandler(),
            PlayerInteractHandler(),
        )
        registerCommands()
        registerEventHandlers()
        usersLocations = UserTiedLocationsManager()
        blocksTiedUsers = BlockTiedUsernamesManager()
//        object : BukkitRunnable() {
//            override fun run() {
//                embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
//            }
//        }.runTaskAsynchronously(this)
    }

    override fun onDisable() {
        TagAPI.onDisable()
    }

    private lateinit var commands: Map<String, CommandExecutor>
    private fun registerCommands() {
        commands.forEach { (commandName, executor) ->
            getCommand(commandName)!!.setExecutor(executor)
        }
    }

    private lateinit var eventHandlers: List<Listener>
    private fun registerEventHandlers() {
        eventHandlers.forEach { listener ->
            server.pluginManager.registerEvents(listener, this)
        }
    }
}

fun Application.module() {
    routingConfiguration()
}