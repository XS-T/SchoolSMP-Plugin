package net.crewco.schoolsmp.commands;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class AirCircleCommand implements CommandExecutor {

    private static final double RADIUS = 5.0; // Radius of the circle
    private static final int PARTICLE_DURATION = 3 * 20; // Particle effect duration in ticks (3 seconds)
    private static final double LIFT_RADIUS = 2.0; // Radius of lifting effect
    private static final double LIFT_STRENGTH = 1.5; // Lifting strength
    private static final int COOLDOWN_SECONDS = 10; // Cooldown time in seconds

    private final HashMap<String, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("mmc.air")) {
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

                                    // Apply lifting effect to entities within the circle
                                    for (Entity entity : circleLocation.getWorld().getNearbyEntities(circleLocation, LIFT_RADIUS, LIFT_RADIUS, LIFT_RADIUS)) {
                                        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                                            ((LivingEntity) entity).setVelocity(new org.bukkit.util.Vector(0, LIFT_STRENGTH, 0));
                                        }
                                    }

                                    // Play cloud particle effect
                                    playAirCircleParticle(circleLocation);
                                }
                                duration += 1;
                            }
                        }
                    }.runTaskTimer(SchoolSMP.getPlugin(), 0, 1);

                    // Set cooldown
                    setCooldown(player.getName(), COOLDOWN_SECONDS);
                    return true;
                } else {
                    player.sendMessage("You must wait " + getCooldown(player.getName()) + " seconds before using this command again!");
                }
            }
        }
        return true;
    }

    private boolean checkCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
            long cooldownEndTime = cooldowns.get(playerName);

            return currentTime >= cooldownEndTime;
        }
        return true;
    }

    private void setCooldown(String playerName, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
        long cooldownEndTime = currentTime + cooldownSeconds;
        cooldowns.put(playerName, cooldownEndTime);
    }

    private long getCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long cooldownEndTime = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds

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

    private void playAirCircleParticle(Location location) {
        if (location.getWorld() != null) {
            location.getWorld().playEffect(location, Effect.CLOUD, 0);
        }
    }
}
