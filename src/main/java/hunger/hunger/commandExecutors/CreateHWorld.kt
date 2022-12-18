package hunger.hunger.commandExecutors

import hunger.hunger.Hunger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CreateHWorld : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (!sender.isOp)
            return false
        Hunger.state.startNewGame()
        return true
    }
}