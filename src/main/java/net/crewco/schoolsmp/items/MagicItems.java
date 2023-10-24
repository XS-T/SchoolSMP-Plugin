package net.crewco.schoolsmp.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MagicItems extends ItemStack {
    public ItemStack AirMagicItem(){
        ItemStack AirItem = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta AirItemMeta = AirItem.getItemMeta();
        AirItemMeta.setDisplayName(ChatColor.WHITE+"Air Element");
        AirItemMeta.addEnchant(Enchantment.ARROW_INFINITE,0,true);
        AirItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        AirItemMeta.setLore(Collections.singletonList("Air-Element"));
        AirItem.setItemMeta(AirItemMeta);
        return AirItem;
    }

    public ItemStack WaterMagicItem(){
        ItemStack WaterItem = new ItemStack(Material.POTION);
        ItemMeta WaterItemMeta = WaterItem.getItemMeta();
        WaterItemMeta.setDisplayName(ChatColor.BLUE+"Water Element");
        WaterItemMeta.addEnchant(Enchantment.ARROW_INFINITE,0,true);
        WaterItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        WaterItemMeta.setLore(Collections.singletonList("Water-Element"));
        WaterItem.setItemMeta(WaterItemMeta);
        return WaterItem;
    }

    public ItemStack EarthMagicItem(){
        ItemStack EarthItem = new ItemStack(Material.DIRT);
        ItemMeta EarthItemMeta = EarthItem.getItemMeta();
        EarthItemMeta.setDisplayName(ChatColor.GOLD+"Earth Element");
        EarthItemMeta.addEnchant(Enchantment.ARROW_INFINITE,0,true);
        EarthItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        EarthItemMeta.setLore(Collections.singletonList("Earth-Element"));
        EarthItem.setItemMeta(EarthItemMeta);
        return EarthItem;
    }

    public ItemStack FireMagicItem(){
        ItemStack FireItem = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta FireItemMeta = FireItem.getItemMeta();
        FireItemMeta.setDisplayName(ChatColor.RED+"Fire Element");
        FireItemMeta.addEnchant(Enchantment.ARROW_INFINITE,0,true);
        FireItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        FireItemMeta.setLore(Collections.singletonList("Fire-Element"));
        FireItem.setItemMeta(FireItemMeta);
        return FireItem;
    }

    public ItemStack PortalMagicItem(){
        ItemStack PortalItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta PortalItemMeta = PortalItem.getItemMeta();
        PortalItemMeta.setDisplayName(ChatColor.DARK_PURPLE+"PortalMagic");
        PortalItemMeta.addEnchant(Enchantment.ARROW_INFINITE,0,true);
        PortalItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PortalItemMeta.setLore(Collections.singletonList("Portal-Magic"));
        PortalItem.setItemMeta(PortalItemMeta);
        return PortalItem;
    }

    public ItemStack VanishMagicItem(){
        ItemStack VanishItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta VanishItemMeta = VanishItem.getItemMeta();
        VanishItemMeta.setDisplayName(ChatColor.GRAY+"VanishMagic");
        VanishItemMeta.addEnchant(Enchantment.ARROW_INFINITE,0,true);
        VanishItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        VanishItemMeta.setLore(Collections.singletonList("Vanish-Magic"));
        VanishItem.setItemMeta(VanishItemMeta);
        return VanishItem;
    }

    public List<ItemStack> MagicItemsList (){
        List<ItemStack> MagicItemList = new ArrayList<>();
        MagicItemList.add(AirMagicItem());
        MagicItemList.add(WaterMagicItem());
        MagicItemList.add(EarthMagicItem());
        MagicItemList.add(FireMagicItem());
        MagicItemList.add(PortalMagicItem());
        MagicItemList.add(VanishMagicItem());
        return MagicItemList;
    }
}
