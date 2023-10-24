package net.crewco.schoolsmp.listeners.Elements;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PortalMagic implements Listener {

    private final Map<Player, Location> teleportationRequests = new HashMap<>();
    private static double TELEPORT_RADIUS = 5.5;
    private static final int COOLDOWN_SECONDS = 15;
    private final Map<String, Long> cooldowns = new HashMap<>();


    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack clickedItem = event.getItem();

        if (clickedItem != null ) {
            if (clickedItem.hasItemMeta()){
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta.getLore().contains("Portal-Magic")){
                    event.setCancelled(true);
                    if (checkCooldown(player.getName())) {
                        player.sendMessage(SchoolSMP.pluginMsg()+"Please enter the teleportation coordinates in chat (x y z [world][radius]):");
                        teleportationRequests.put(player, player.getLocation());
                    } else {
                        player.sendMessage(SchoolSMP.pluginMsg()+"You must wait " + getCooldown(player.getName()) + " seconds before using this command again!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (teleportationRequests.containsKey(player)) {
            event.setCancelled(true);

            String[] input = event.getMessage().split(" ");
            if (input.length >= 3) {
                try {
                    double x = Double.parseDouble(input[0]);
                    double y = Double.parseDouble(input[1]);
                    double z = Double.parseDouble(input[2]);

                    World targetWorld = (input.length >= 4) ? Bukkit.getWorld(input[3]) : player.getWorld();
                    if (targetWorld == null) {
                        player.sendMessage(SchoolSMP.pluginMsg()+"Invalid target world.");
                        teleportationRequests.remove(player);
                        return;
                    }
                    if (input.length >= 5){
                        if (Double.parseDouble(input[4]) >= 6 || Double.parseDouble(input[4]) <= 0){
                            player.sendMessage(SchoolSMP.pluginMsg()+"You can only have a circle radius of 1-10");
                            return;
                        }else if (Objects.equals(input[4], "")){
                            TELEPORT_RADIUS = Double.parseDouble(input[4]);
                        }
                    }

                    Location teleportLocation = new Location(targetWorld, x, y, z);

                    World world = player.getWorld();
                    for (Entity nearbyEntity : world.getNearbyEntities(player.getLocation(), TELEPORT_RADIUS, TELEPORT_RADIUS, TELEPORT_RADIUS)) {
                        if (nearbyEntity instanceof Player) {
                            Player nearbyPlayer = (Player) nearbyEntity;
                            if (!nearbyPlayer.equals(player)) {
                                teleportPlayerSync(nearbyPlayer, teleportLocation);
                                nearbyPlayer.sendMessage(SchoolSMP.pluginMsg()+"You have been teleported to the specified location.");
                                playPortalParticles(nearbyPlayer.getLocation());
                            }
                        } else if (nearbyEntity instanceof LivingEntity && !(nearbyEntity instanceof Player)) {
                            LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                            teleportEntitySync(livingEntity, teleportLocation);
                            playPortalParticles(livingEntity.getLocation());
                        }
                    }



                    // Teleport the player who clicked
                    playPortalParticles(player.getLocation());
                    teleportPlayerSync(player, teleportLocation);
                    playPortalParticles(teleportLocation);

                    // Clear the teleportation request after handling it
                    teleportationRequests.remove(player);
                    setCooldown(player.getName());
                } catch (NumberFormatException e) {
                    player.sendMessage(SchoolSMP.pluginMsg()+"Invalid coordinates. Please enter valid numbers for x, y, and z.");
                    teleportationRequests.remove(player);
                }
            } else {
                player.sendMessage(SchoolSMP.pluginMsg()+"Invalid input. Please provide x, y, and z coordinates.");
                teleportationRequests.remove(player);
            }
        }
    }

    private void teleportPlayerSync(Player player, Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(location);
            }
        }.runTask(SchoolSMP.getPlugin()); // Replace YourPluginClass with the appropriate class name
    }

    private void teleportEntitySync(LivingEntity entity, Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                entity.teleport(location);
            }
        }.runTask(SchoolSMP.getPlugin()); // Replace YourPluginClass with the appropriate class name
    }

    private boolean checkCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
            return currentTime - lastUsage >= COOLDOWN_SECONDS;
        }
        return true;
    }

    private void setCooldown(String playerName) {
        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
        cooldowns.put(playerName, currentTime);
    }

    private long getCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
            long cooldownEndTime = lastUsage + COOLDOWN_SECONDS;

            if (currentTime < cooldownEndTime) {
                // If the cooldown has not expired yet, return the remaining time in seconds
                return cooldownEndTime - currentTime;
            } else {
                // If the cooldown has expired, remove the player from cooldowns map
                cooldowns.remove(playerName);
            }
        }
        // If there's no cooldown or the cooldown has expired, return 0
        return 0;
    }

    private void teleportPlayers(Location location) {
        World world = location.getWorld();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld()) &&
                    player.getLocation().distanceSquared(location) <= TELEPORT_RADIUS * TELEPORT_RADIUS) {
                player.teleport(location);
                playPortalParticles(player.getLocation());
            }
        }
        playPortalParticles(location);
    }

    private void playPortalParticles(Location center) {
        World world = center.getWorld();
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double xOffset = TELEPORT_RADIUS * Math.cos(angle);
            double zOffset = TELEPORT_RADIUS * Math.sin(angle);
            Location particleLocation = center.clone().add(xOffset, 1, zOffset);
            world.playEffect(particleLocation, Effect.ENDER_SIGNAL, 0);
        }
    }
}
