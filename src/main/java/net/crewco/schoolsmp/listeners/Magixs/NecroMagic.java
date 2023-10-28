package net.crewco.schoolsmp.listeners.Magixs;

import net.crewco.schoolsmp.SchoolSMP;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class NecroMagic implements Listener {

    private static final double SUMMON_RADIUS = 5.0;
    private static final int COOLDOWN_SECONDS = 30;
    private static final int MINION_LIFESPAN_SECONDS = 60;
    private final HashMap<String, Long> cooldowns = new HashMap<>();
    private final HashMap<String, Integer> activeMinions = new HashMap<>();
    private final int numberOfMinionsToSpawn = 3; // Set the number of minions to spawn

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack necromancyItem = event.getItem();

        if (necromancyItem != null && necromancyItem.hasItemMeta()) {
            ItemMeta itemMeta = necromancyItem.getItemMeta();
            if (itemMeta.getLore().contains("Necro-Magix")) {
                if (SchoolSMP.helper().isInRegion(player,"spawn")){
                    player.sendMessage(SchoolSMP.pluginMsg()+"You can not use magic here");
                    return;
                }
                if (activeMinions.containsKey(player.getName()) && activeMinions.get(player.getName()) >= numberOfMinionsToSpawn) {
                    player.sendMessage("You already have " + numberOfMinionsToSpawn + " active minions. Wait for them to expire before summoning more.");
                    return;
                }

                if (checkCooldown(player.getName())) {
                    summonWitherSkeletons(player);
                    setCooldown(player.getName());

                    int activeMinionsCount = activeMinions.getOrDefault(player.getName(), 0);
                    activeMinions.put(player.getName(), activeMinionsCount + numberOfMinionsToSpawn);
                } else {
                    long remainingCooldown = getCooldown(player.getName());
                    player.sendMessage("You must wait " + remainingCooldown + " seconds before using this spell again!");
                }
            }
        }
    }

    private boolean checkCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis() / 1000;
            return currentTime - lastUsage >= COOLDOWN_SECONDS;
        }
        return true;
    }

    private void setCooldown(String playerName) {
        long currentTime = System.currentTimeMillis() / 1000;
        cooldowns.put(playerName, currentTime);
    }

    private long getCooldown(String playerName) {
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis() / 1000;
            long cooldownEndTime = lastUsage + COOLDOWN_SECONDS;

            if (currentTime < cooldownEndTime) {
                return cooldownEndTime - currentTime;
            } else {
                cooldowns.remove(playerName);
            }
        }
        return 0;
    }

    private void summonWitherSkeletons(Player player) {
        playParticleEffect(player.getLocation());
        World world = player.getWorld();
        Location summonLocation = player.getLocation();

        for (int i = 0; i < numberOfMinionsToSpawn; i++) {
            Location mobLocation = getRandomLocationInRadius(summonLocation, SUMMON_RADIUS);
            Entity skeletonEntity = world.spawnEntity(mobLocation, EntityType.SKELETON);

            if (skeletonEntity instanceof LivingEntity) {
                LivingEntity skeleton = (LivingEntity) skeletonEntity;
                skeleton.setRemoveWhenFarAway(true);
                skeleton.setCanPickupItems(false);

                if (skeleton instanceof Creature) {
                    LivingEntity nearestEnemy = findNearestEnemy(player.getLocation(),player);
                    if (nearestEnemy != null && nearestEnemy != player) {
                        ((Creature) skeleton).setTarget(nearestEnemy);
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        skeleton.remove();

                        int activeMinionsCount = activeMinions.getOrDefault(player.getName(), 0);
                        if (activeMinionsCount > 0) {
                            activeMinions.put(player.getName(), activeMinionsCount - 1);
                        }
                    }
                }.runTaskLater(SchoolSMP.getPlugin(), MINION_LIFESPAN_SECONDS * 20);
            }

            skeletonEntity.setCustomName("Necromancer Minion");
        }

        player.sendMessage("You have summoned " + numberOfMinionsToSpawn + " wither skeletons to aid you.");
    }

    private Location getRandomLocationInRadius(Location center, double radius) {
        double randomAngle = Math.random() * 2 * Math.PI;
        double randomRadius = Math.random() * radius;
        double xOffset = randomRadius * Math.cos(randomAngle);
        double zOffset = randomRadius * Math.sin(randomAngle);
        return center.clone().add(xOffset, 0, zOffset);
    }

    private void playParticleEffect(Location center) {
        new BukkitRunnable() {
            double angleTheta = 0;
            double anglePhi = 0;

            @Override
            public void run() {
                double x = center.getX() + SUMMON_RADIUS * Math.sin(anglePhi) * Math.cos(angleTheta);
                double y = center.getY() + SUMMON_RADIUS * Math.cos(anglePhi);
                double z = center.getZ() + SUMMON_RADIUS * Math.sin(anglePhi) * Math.sin(angleTheta);

                Location particleLocation = new Location(center.getWorld(), x, y, z);

                center.getWorld().playEffect(particleLocation, Effect.INSTANT_SPELL, 1);

                angleTheta += Math.PI / 8;
                if (angleTheta >= 2 * Math.PI) {
                    angleTheta = 0;
                    anglePhi += Math.PI / 8;
                    if (anglePhi >= Math.PI) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(SchoolSMP.getPlugin(), 0, 2);
    }

    private LivingEntity findNearestEnemy(Location location,Player summoner) {
        double nearestDistanceSquared = Double.MAX_VALUE;
        LivingEntity nearestEnemy = null;

        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof LivingEntity && entity != summoner && entity.getType() != EntityType.SKELETON) {
                double distanceSquared = entity.getLocation().distanceSquared(location);
                if (distanceSquared < nearestDistanceSquared) {
                    nearestDistanceSquared = distanceSquared;
                    nearestEnemy = (LivingEntity) entity;
                }
            }
        }

        return nearestEnemy;
    }
}
