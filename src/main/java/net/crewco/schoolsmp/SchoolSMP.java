package net.crewco.schoolsmp;

import net.crewco.schoolsmp.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchoolSMP extends JavaPlugin {

    private static SchoolSMP plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Elements
        getCommand("mmc-fire").setExecutor(new FireCircleCommand());
        getCommand("mmc-water").setExecutor(new WaterCircleCommand());
        getCommand("mmc-earth").setExecutor(new EarthCircleCommand());
        getCommand("mmc-air").setExecutor(new AirCircleCommand());

        //Misc
        getCommand("mmc-portal").setExecutor(new TeleportationCircleCommand());
        getCommand("mmc-vanish").setExecutor(new VanishCircleCommand());

        //Logger
        getLogger().info("Your plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Your plugin has been disabled!");
    }


    public static SchoolSMP getPlugin() {
        return plugin;
    }
}
