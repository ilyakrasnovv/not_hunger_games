package hunger.hunger.commandExecutors

import hunger.hunger.Hunger
import hunger.hunger.utilities.generateHWorld
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.text.SimpleDateFormat
import java.util.*

/**
 * handler of /createhworld
 * to be removed
 */
class CreateHWorld : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (!sender.isOp)
            return false
        val world = generateHWorld(args!![0].toInt())
        sender.sendMessage("World ${world.name} created, ${args.toList()}")
        return true
    }
}