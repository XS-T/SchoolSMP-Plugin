package net.crewco.schoolsmp.listeners;

import net.crewco.schoolsmp.items.MagicItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class PreventTrade implements Listener {

    MagicItems magicItems = new MagicItems();
    @EventHandler
    public void onPreventTrade(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && !Objects.equals(event.getView().getTitle(), player.getInventory().getTitle())) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && magicItems.MagicItemsList().contains(clickedItem)) {
                event.setCancelled(true);
            }
        }
    }
}
