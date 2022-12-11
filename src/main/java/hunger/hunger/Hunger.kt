package hunger.hunger

import hunger.hunger.commandExecutors.CreateHWorld
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin

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