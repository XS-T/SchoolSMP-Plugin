package net.crewco.schoolsmp.listeners.Elements;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class WaterElement implements Listener {

    private static final double RADIUS = 5.0; // Radius of the circle
    private static final int PARTICLE_DURATION = 3 * 20; // Particle effect duration in ticks (3 seconds)
    private static final double DROWNING_RADIUS = 2.0; // Radius of drowning effect
    private static final double DROWNING_DAMAGE = 2.0; // Amount of damage to apply
    private static final int COOLDOWN_SECONDS = 10; // Cooldown time in seconds

    private final HashMap<String, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            ItemStack Water_element = event.getItem();
            if (Water_element != null) {
                if (Water_element.hasItemMeta()) {
                    ItemMeta water_elementItemMeta = Water_element.getItemMeta();
                    if (water_elementItemMeta.getLore().contains("Water-Element")) {
                        if (SchoolSMP.helper().isInRegion(player,"spawn")){
                            player.sendMessage(SchoolSMP.pluginMsg()+"You can not use magic here");
                            return;
                        }
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

                                            // Apply drowning effect to living entities within the radius
                                            for (Entity entity : circleLocation.getWorld().getNearbyEntities(circleLocation, DROWNING_RADIUS, DROWNING_RADIUS, DROWNING_RADIUS)) {
                                                if (entity instanceof Player && !entity.equals(player) && entity.getLocation().distanceSquared(circleLocation) <= RADIUS * RADIUS) {
                                                    ((LivingEntity) entity).damage(DROWNING_DAMAGE);
                                                }
                                            }

                                            // Play water particle effect
                                            playWaterCircleParticle(circleLocation);
                                        }
                                        duration += 1;
                                    }
                                }
                            }.runTaskTimer(SchoolSMP.getPlugin(), 0, 1);

                            // Set cooldown
                            setCooldown(player.getName());
                        } else {
                            player.sendMessage(SchoolSMP.pluginMsg()+"You must wait " + (COOLDOWN_SECONDS - getCooldown(player.getName())) + " seconds before using this command again!");
                        }
                    }
                }
            }
        }catch(NullPointerException e){}
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

    private void playWaterCircleParticle(Location location) {
        if (location.getWorld() != null) {
            location.getWorld().playEffect(location, Effect.WATERDRIP, 0);
        }
    }
}
