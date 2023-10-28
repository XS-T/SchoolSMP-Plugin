package net.crewco.schoolsmp.listeners.Magixs;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishMagic implements Listener {

    private static final int VANISH_DURATION_SECONDS = 10; // Vanish duration in seconds (adjust as needed)
    private static final long COOLDOWN_SECONDS = 30; // Cooldown time in seconds (adjust as needed)

    private final Set<UUID> hiddenPlayers = new HashSet<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            ItemStack VanishMagic = event.getItem();
            if (VanishMagic != null) {
                if (VanishMagic.hasItemMeta()) {
                    ItemMeta fire_elementItemMeta = VanishMagic.getItemMeta();
                    if (fire_elementItemMeta.getLore().contains("Vanish-Magix")) {
                        if (SchoolSMP.helper().isInRegion(player,"spawn")){
                            player.sendMessage(SchoolSMP.pluginMsg()+"You can not use magixs here");
                            return;
                        }
                        if (hiddenPlayers.contains(playerUUID)) {
                            // Player is already hidden, cancel vanish and put them on cooldown
                            hiddenPlayers.remove(playerUUID);
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.showPlayer(player);
                            }
                            player.sendMessage(SchoolSMP.pluginMsg()+"Your vanish effect has been canceled and you are now visible to others.");
                            setCooldown(playerUUID);
                        } else if (isCooldownActive(playerUUID)) {
                            // Player is on cooldown
                            player.sendMessage(SchoolSMP.pluginMsg()+"You must wait " + getCooldown(player) + " before using this command again.");
                        } else {
                            // Hide the player for all online players
                            hiddenPlayers.add(playerUUID);
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.hidePlayer(player);
                            }
                            player.sendMessage(SchoolSMP.pluginMsg()+"You are now hidden from others.");

                            // Play particles and set a delay to revert the vanish effect after the specified duration
                            playVanishParticles(player.getLocation(), VANISH_DURATION_SECONDS);
                            Bukkit.getScheduler().runTaskLater(SchoolSMP.getPlugin(), () -> {
                                if (hiddenPlayers.contains(playerUUID)) {
                                    hiddenPlayers.remove(playerUUID);
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        onlinePlayer.showPlayer(player);
                                    }
                                    player.sendMessage(SchoolSMP.pluginMsg()+"Your vanish effect has expired.");
                                    playVanishParticles(player.getLocation(), VANISH_DURATION_SECONDS);
                                }
                            }, VANISH_DURATION_SECONDS * 20); // 1 second = 20 ticks
                        }
                    }
                }
            }
        }catch(NullPointerException e){}
    }

    private void setCooldown(UUID playerUUID) {
        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
        cooldowns.put(playerUUID, currentTime);
    }

    private String getCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long lastUsage = cooldowns.get(playerUUID);
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
            long remainingTime = lastUsage + COOLDOWN_SECONDS - currentTime;
            if (remainingTime <= 0) {
                cooldowns.remove(playerUUID);
                return "0 seconds";
            }
            return remainingTime + " seconds";
        }
        return "0 seconds";
    }

    private boolean isCooldownActive(UUID playerUUID) {
        if (cooldowns.containsKey(playerUUID)) {
            long lastUsage = cooldowns.get(playerUUID);
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
            return currentTime - lastUsage < COOLDOWN_SECONDS;
        }
        return false;
    }

    private void playVanishParticles(Location location, int durationSeconds) {
        World world = location.getWorld();
        double radius = 1.5;
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);
            Location particleLocation = location.clone().add(xOffset, 1, zOffset);
            world.playEffect(particleLocation, Effect.SMOKE, 0);
        }
    }


}
