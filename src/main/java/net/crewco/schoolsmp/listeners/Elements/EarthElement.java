package net.crewco.schoolsmp.listeners.Elements;

import net.crewco.schoolsmp.SchoolSMP;
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

public class EarthElement implements Listener {


    private static final double RADIUS = 5.0;
    private static final int PARTICLE_DURATION = 3 * 20;
    private static final int DAMAGE_AMOUNT = 1; // Damage amount to players inside the circle
    private static final int COOLDOWN_SECONDS = 10;

    private final HashMap<String, Long> cooldowns = new HashMap<>();


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            ItemStack earth_element = event.getItem();
            if (earth_element != null){
            if (earth_element.hasItemMeta()) {
                ItemMeta fire_elementItemMeta = earth_element.getItemMeta();
                if (fire_elementItemMeta.getLore().contains("Earth-Element")) {
                    if (checkCooldown(player.getName())) {
                        Location center = player.getLocation().clone().add(0, 1, 0);
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
                                        Location circleLocation = new Location(center.getWorld(), x, center.getY(), z);

                                        for (Entity entity : circleLocation.getChunk().getEntities()) {
                                            if (entity instanceof Player && !entity.equals(player) &&
                                                    entity.getLocation().distanceSquared(circleLocation) <= RADIUS * RADIUS) {
                                                ((Player) entity).damage(DAMAGE_AMOUNT);
                                            }
                                        }
                                        playEarthCircleParticle(circleLocation);
                                    }
                                    duration += 1;
                                }
                            }
                        }.runTaskTimer(SchoolSMP.getPlugin(), 0, 1);
                        setCooldown(player.getName());
                    } else {
                        player.sendMessage(SchoolSMP.pluginMsg()+"You must wait " + getCooldown(player.getName()) + " seconds before using this command again!");
                    }
                }
            }
        }
    }catch (NullPointerException e){}
    }


    private boolean checkCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long currentTime = System.currentTimeMillis() / 1000;
            long cooldownEndTime = cooldowns.get(playerName);
            return cooldownEndTime <= currentTime;
        }
        return true;
    }

    private void setCooldown(String playerName) {
        long currentTime = System.currentTimeMillis() / 1000;
        long cooldownEndTime = currentTime + COOLDOWN_SECONDS;
        cooldowns.put(playerName, cooldownEndTime);
    }

    private long getCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long cooldownEndTime = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime < cooldownEndTime) {
                return cooldownEndTime - currentTime;
            } else {
                cooldowns.remove(playerName);
            }
        }
        return 0;
    }

    private void playEarthCircleParticle(Location location) {
        if (location.getWorld() != null) {
            location.getWorld().playEffect(location, Effect.SMOKE, 0);
        }
    }


}
