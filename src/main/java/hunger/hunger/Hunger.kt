package hunger.hunger

import com.lkeehl.tagapi.TagAPI
import hunger.hunger.commandExecutors.CreateHWorld
import hunger.hunger.dataManaging.BlockTiedUsernamesManager
import hunger.hunger.dataManaging.Config
import hunger.hunger.dataManaging.UserTiedLocationsManager
import hunger.hunger.eventHandlers.*
import hunger.hunger.models.Dispatcher
import hunger.hunger.models.GameState
import hunger.hunger.web.WebStateProvider
import hunger.hunger.web.routingConfiguration
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File

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
        lateinit var hConfig: Config
        val client = HttpClient(CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }
        lateinit var logData: File
    }

    override fun onEnable(
    ) {
        instance = this
        hConfig = Config()
        TagAPI.onEnable(this)
        state = GameState(WebStateProvider(), object : Dispatcher() {

            init {
                logData = File(instance.dataFolder, "events_data.json")
                logData.createNewFile()
            }

            override fun sendData(content: String) {
                runBlocking {
                    client.post("${hConfig.LOGIC_SERVER_URL}/game_state/event") {
                        parameter("token", hConfig.LOGIC_SERVER_ACCESS_TOKEN)
                        synchronized(logData) {
                            logData.appendText(content)
                        }
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
            PlayerPortalHandler(),
        )
        registerCommands()
        registerEventHandlers()
        usersLocations = UserTiedLocationsManager()
        blocksTiedUsers = BlockTiedUsernamesManager()
        object : BukkitRunnable() {
            override fun run() {
                embeddedServer(
                    Netty,
                    port = hConfig.PORT,
                    host = "0.0.0.0",
                    module = Application::module
                ).start(wait = true)
            }
        }.runTaskAsynchronously(this)
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
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json()
    }
    routingConfiguration()
}