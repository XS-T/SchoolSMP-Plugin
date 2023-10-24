package net.crewco.schoolsmp.commands;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class FireCircleCommand implements CommandExecutor {

    private static final double RADIUS = 5.0; // Radius of the circle
    private static final int FIRE_TICKS = 100; // Fire effect duration in ticks (5 seconds)
    private static final int COOLDOWN_SECONDS = 10; // Cooldown time in seconds
    private static final int PARTICLE_DURATION = 3 * 20; // Particle effect duration in ticks (3 seconds)

    private final HashMap<String, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("mmc.fire")) {
                if (checkCooldown(player.getName())) {
                    World world = player.getWorld();
                    Location center = player.getLocation().clone().add(0, 1, 0); // Adjust the height if needed

                    new BukkitRunnable() {
                        int duration = 0;

                        @Override
                        public void run() {
                            if (duration >= PARTICLE_DURATION) {
                                this.cancel();
                            } else {
                                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 18) {
                                    double x = center.getX() + RADIUS * Math.cos(angle);
                                    double z = center.getZ() + RADIUS * Math.sin(angle);
                                    Location circleLocation = new Location(world, x, center.getY(), z);

                                    // Adjust the height of the circle according to your needs
                                    for (Entity entity : circleLocation.getChunk().getEntities()) {
                                        if (entity instanceof Player && !entity.equals(player) && entity.getLocation().distanceSquared(circleLocation) <= RADIUS * RADIUS) {
                                            // Set other entities (mobs, etc.) within the radius on fire for 5 seconds (100 ticks)
                                            entity.setFireTicks(FIRE_TICKS);
                                        }
                                    }

                                    // Play particle effect
                                    playCircleParticle(circleLocation);
                                }
                                duration += 1;
                            }
                        }
                    }.runTaskTimer(SchoolSMP.getPlugin(), 0, 1); // Use your plugin instance to schedule the task

                    // Set cooldown
                    setCooldown(player.getName());
                } else {
                    player.sendMessage("You must wait " + (COOLDOWN_SECONDS - getCooldown(player.getName())) + " seconds before using this command again!");
                }
            }
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
                return currentTime-cooldownEndTime+10;
            } else {
                // If the cooldown has expired, remove the player from cooldowns map
                cooldowns.remove(playerName);
            }
        }
        // If there's no cooldown or the cooldown has expired, return 0
        return 0;
    }

    private void playCircleParticle(Location location) {
        if (location.getWorld() != null) {
            // Play particle effect at the specified location
            location.getWorld().playEffect(location, Effect.FLAME, 0);
        }
    }
}
