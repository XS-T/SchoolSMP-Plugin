package net.crewco.schoolsmp.listeners;

import net.crewco.schoolsmp.SchoolSMP;
import net.crewco.schoolsmp.items.MagicItems;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomMagics implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        List<String> items = Arrays.asList("Air","Water","Fire","Earth","Vanish","Portal","Necro");
        String randomItem = getRandomItem(items);
        Player player = event.getPlayer();
        MagicItems magicItems = new MagicItems();


        if (!player.hasPlayedBefore()){
            if (randomItem.equals("Air")){
                player.sendMessage(SchoolSMP.pluginMsg()+"You can control the Air element");
                player.getInventory().addItem(magicItems.AirMagicItem());
            } else if (randomItem.equals("Water")) {
                player.sendMessage(SchoolSMP.pluginMsg()+"You can control the Water element");
                player.getInventory().addItem(magicItems.WaterMagicItem());
            } else if (randomItem.equals("Fire")) {
                player.sendMessage(SchoolSMP.pluginMsg()+"You can control the Fire element");
                player.getInventory().addItem(magicItems.FireMagicItem());
            }else if (randomItem.equals("Earth")){
                player.sendMessage(SchoolSMP.pluginMsg()+"You can control the Earth element");
                player.getInventory().addItem(magicItems.EarthMagicItem());
            }else if (randomItem.equals("Vanish")){
                player.sendMessage(SchoolSMP.pluginMsg()+"You have Vanish magix");
                player.getInventory().addItem(magicItems.VanishMagicItem());
            } else if (randomItem.equals("Portal")) {
                player.sendMessage(SchoolSMP.pluginMsg()+"You have Portal magix");
                player.getInventory().addItem(magicItems.PortalMagicItem());
            }else if (randomItem.equals("Necro")){
                player.sendMessage(SchoolSMP.pluginMsg()+"You have Necromancy magix");
                player.getInventory().addItem(magicItems.NecroMagicItem());
            }
        }


    }

    private static final Random random = new Random();

    public static <T> T getRandomItem(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}
