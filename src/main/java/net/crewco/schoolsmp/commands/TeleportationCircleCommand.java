package net.crewco.schoolsmp.commands;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class TeleportationCircleCommand implements CommandExecutor {

    private static final double TELEPORT_RADIUS = 5.0; // Radius of the teleportation circle
    private static final int PORTAL_PARTICLE_DURATION = 3 * 20; // Particle effect duration in ticks (3 seconds)
    private static final int COOLDOWN_SECONDS = 15; // Cooldown time in seconds

    private final HashMap<String, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("mmc.portal")) {
                if (args.length < 3 || args.length > 4) {
                    player.sendMessage("Usage: /mmc-portal <x> <y> <z> [world]");
                    return true;
                }

                if (checkCooldown(player.getName())) {
                    try {
                        double x = Double.parseDouble(args[0]);
                        double y = Double.parseDouble(args[1]);
                        double z = Double.parseDouble(args[2]);

                        World targetWorld = (args.length == 4) ? Bukkit.getWorld(args[3]) : player.getWorld();
                        if (targetWorld == null) {
                            player.sendMessage("Invalid target world.");
                            return true;
                        }

                        Location teleportLocation = new Location(targetWorld, x, y, z);

                        // Check if there are nearby players within the teleportation radius
                        for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                            if (nearbyPlayer != player &&
                                    nearbyPlayer.getWorld() != null && player.getWorld() != null &&
                                    nearbyPlayer.getWorld().equals(player.getWorld()) &&
                                    nearbyPlayer.getLocation().distanceSquared(player.getLocation()) <= TELEPORT_RADIUS * TELEPORT_RADIUS) {
                                // Teleport nearby players to the specified location
                                nearbyPlayer.teleport(teleportLocation);
                                nearbyPlayer.sendMessage("You have been teleported to the specified location.");
                                player.sendMessage("Teleported nearby players and yourself to the specified location.");
                                playPortalParticles(nearbyPlayer.getLocation());
                            }
                        }

                        // Teleport the player who triggered the command
                        playPortalParticles(player.getLocation());
                        player.teleport(teleportLocation);
                        playPortalParticles(teleportLocation);

                        // Set cooldown
                        setCooldown(player.getName());
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage("Invalid coordinates. Please enter valid numbers for x, y, and z.");
                    }
                } else {
                    player.sendMessage("You must wait " + getCooldown(player.getName()) + " seconds before using this command again!");
                }
            } else {
                player.sendMessage("You don't have permission to use this command.");
            }
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
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
