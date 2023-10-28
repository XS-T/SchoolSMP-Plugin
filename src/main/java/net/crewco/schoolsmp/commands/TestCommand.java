package net.crewco.schoolsmp.commands;

import net.crewco.schoolsmp.Util.WorldGuardHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            WorldGuardHelper helper = new WorldGuardHelper();
            if (helper.isInRegion(player,"spawn")){
                player.sendMessage("Your in spawn");
            }
        }
        return true;
    }
}
