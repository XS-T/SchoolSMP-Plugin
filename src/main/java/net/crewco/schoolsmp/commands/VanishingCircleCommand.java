package net.crewco.schoolsmp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class VanishingCircleCommand implements CommandExecutor {

    private static final double VANISH_RADIUS = 1.5; // Radius of the vanishing circle
    private static final int VANISH_DURATION = 11 * 20; // Vanish effect duration in ticks (10 seconds)
    private static final int COOLDOWN_SECONDS = 15; // Cooldown time in seconds

    private final HashMap<String, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("mmc.vanish")) {
                if (checkCooldown(player.getName())) {
                    World world = player.getWorld();
                    Location center = player.getLocation().clone().add(0, 1, 0); // Adjust the height if needed

                    // Apply to vanish effect to the player
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, VANISH_DURATION, 0));
                    playVanishParticle(player.getLocation());

                    // Play vanishing circle particles and apply effect to nearby players
                    for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                        if (nearbyPlayer != player &&
                                nearbyPlayer.getLocation().distanceSquared(center) <= VANISH_RADIUS * VANISH_RADIUS) {
                            // Apply to vanish effect to nearby players
                            nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, VANISH_DURATION, 0));
                            // Play particle effect
                            playVanishParticle(nearbyPlayer.getLocation());
                            player.sendMessage("You vanished yourself and nearby players within the radius.");
                        }
                    }

                    // Set cooldown
                    setCooldown(player.getName());
                    return true;
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

    private void playVanishParticle(Location location) {
        World world = location.getWorld();
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double xOffset = VANISH_RADIUS * Math.cos(angle);
            double zOffset = VANISH_RADIUS * Math.sin(angle);
            Location particleLocation = location.clone().add(xOffset, 1, zOffset);
            world.playEffect(particleLocation, Effect.SMOKE, 0);
        }
    }
}
