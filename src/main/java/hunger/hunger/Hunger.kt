package hunger.hunger

import hunger.hunger.commandExecutors.CreateHWorld
import hunger.hunger.dataManaging.BlockTiedUsernamesManager
import hunger.hunger.dataManaging.UserTiedLocationsManager
import hunger.hunger.eventHandlers.*
import hunger.hunger.mock.StateProviderHardcodeMock
import hunger.hunger.models.Dispatcher
import hunger.hunger.models.GameState
import hunger.hunger.web.routingConfiguration
import io.ktor.server.application.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

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
    }

    override fun onEnable() {
        instance = this
        state = GameState(StateProviderHardcodeMock(), object : Dispatcher() {
            override fun sendData(content: String) {
                logger.log(Level.INFO, content)
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
        // Plugin shutdown logic
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