package net.crewco.schoolsmp.Util;


import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WorldGuardHelper {
    //Is in region
    public boolean isInRegion(Player player, String id) {
        World world = player.getWorld();
        for (ProtectedRegion region : WGBukkit.getRegionManager(world).getApplicableRegions(player.getLocation())) {
            if (region.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
