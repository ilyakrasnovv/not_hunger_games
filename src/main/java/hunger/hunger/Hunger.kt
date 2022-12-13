package hunger.hunger

import hunger.hunger.commandExecutors.CreateHWorld
import hunger.hunger.web.routingConfiguration
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.logging.Level

/**
 * Not Hunger Games plugin
 * @author ilyakrasnovv
 */
class Hunger : JavaPlugin() {
    companion object {
        lateinit var instance: Hunger
    }

    override fun onEnable() {
        instance = this
        commands = mapOf(
            "createHWorld" to CreateHWorld()
        )
        registerCommands()
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
}

fun Application.module() {
    routingConfiguration()
}