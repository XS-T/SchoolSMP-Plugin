package net.crewco.schoolsmp.commands.app_command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApplicationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage("Application Link: https://forms.gle/WDFNZdU12DXzsQrQ7");
        }
        return true;
    }
}
