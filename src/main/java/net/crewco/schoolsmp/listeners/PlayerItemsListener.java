package net.crewco.schoolsmp.listeners;

import net.crewco.schoolsmp.SchoolSMP;
import net.crewco.schoolsmp.items.MagicItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PlayerItemsListener implements Listener {

    private HashMap<UUID,ItemStack> soulbound = new HashMap<>();
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (event.getEntity() != null){
            Player player = event.getEntity();
            MagicItems magicItems = new MagicItems();
            for (ItemStack magicItem : magicItems.MagicItemsList()){
                if (event.getEntity().getInventory().contains(magicItem)){
                    event.getDrops().remove(magicItem);
                    if (!soulbound.containsKey(event.getEntity().getUniqueId())){
                        soulbound.put(event.getEntity().getUniqueId(),magicItem);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        if (soulbound.containsKey(event.getPlayer().getUniqueId())){
            Player player = event.getPlayer();
            ItemStack item = soulbound.get(player.getUniqueId());
            if (!player.getInventory().contains(item)){
                player.getInventory().addItem(item);
                soulbound.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if (event.getPlayer() != null){
            Player player = event.getPlayer();
            MagicItems magicItems = new MagicItems();
            if (magicItems.MagicItemsList().contains(event.getItemDrop().getItemStack())){
                event.setCancelled(true);
                player.sendMessage(SchoolSMP.pluginMsg()+"You can not drop this item");
            }
        }
    }
}
