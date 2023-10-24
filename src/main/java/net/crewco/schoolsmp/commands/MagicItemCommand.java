package net.crewco.schoolsmp.commands;

import net.crewco.schoolsmp.items.MagicItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MagicItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("mmc.portal")) {
                if (args.length < 1) {
                    player.sendMessage("Usage: /magic-item <magicitem>");
                    return true;
                }
                MagicItems magicItems = new MagicItems();
                if (args[0].contains("Air".toLowerCase())){
                    player.getInventory().addItem(magicItems.AirMagicItem());
                } else if (args[0].contains("Water".toLowerCase())) {
                    player.getInventory().addItem(magicItems.WaterMagicItem());
                } else if (args[0].contains("Earth".toLowerCase())) {
                    player.getInventory().addItem(magicItems.EarthMagicItem());
                } else if (args[0].contains("Fire".toLowerCase())) {
                    player.getInventory().addItem(magicItems.FireMagicItem());
                } else if (args[0].contains("Vanish".toLowerCase())) {
                    player.getInventory().addItem(magicItems.VanishMagicItem());
                } else if (args[0].contains("Portal".toLowerCase())) {
                    player.getInventory().addItem(magicItems.PortalMagicItem());
                }
            }
        }
        return true;
    }
}
