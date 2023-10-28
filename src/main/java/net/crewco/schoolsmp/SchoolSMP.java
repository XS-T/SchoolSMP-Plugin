package net.crewco.schoolsmp;


import net.crewco.schoolsmp.Util.WorldGuardHelper;
import net.crewco.schoolsmp.commands.*;
import net.crewco.schoolsmp.commands.app_command.ApplicationCommand;
import net.crewco.schoolsmp.listeners.Elements.*;
import net.crewco.schoolsmp.listeners.Magixs.NecroMagic;
import net.crewco.schoolsmp.listeners.Magixs.PortalMagic;
import net.crewco.schoolsmp.listeners.Magixs.VanishMagic;
import net.crewco.schoolsmp.listeners.PlayerItemsListener;
import net.crewco.schoolsmp.listeners.PreventTrade;
import net.crewco.schoolsmp.listeners.RandomMagics;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchoolSMP extends JavaPlugin {

    private static SchoolSMP plugin;
    private static String plmsg;
    private static WorldGuardHelper helper;




    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        plmsg = ChatColor.translateAlternateColorCodes('&',"&7[&4SchoolSMP&7]> ");
        //WorldGuard
        helper = new WorldGuardHelper();


        //Elements
        getCommand("mmc-fire").setExecutor(new FireCircleCommand());
        getCommand("mmc-water").setExecutor(new WaterCircleCommand());
        getCommand("mmc-earth").setExecutor(new EarthCircleCommand());
        getCommand("mmc-air").setExecutor(new AirCircleCommand());

        //Mgixs
        getCommand("mmc-portal").setExecutor(new TeleportationCircleCommand());
        getCommand("mmc-vanish").setExecutor(new VanishCircleCommand());

        //TestCommands
        //getCommand("test").setExecutor(new TestCommand());

        //Elements Listeners
        getServer().getPluginManager().registerEvents(new FireElement(),this);
        getServer().getPluginManager().registerEvents(new WaterElement(),this);
        getServer().getPluginManager().registerEvents(new EarthElement(),this);
        getServer().getPluginManager().registerEvents(new AirElement(),this);
        //Magixs
        getServer().getPluginManager().registerEvents(new PortalMagic(),this);
        getServer().getPluginManager().registerEvents(new VanishMagic(),this);
        getServer().getPluginManager().registerEvents(new NecroMagic(),this);

        //Elements Commands
        getCommand("magic-item").setExecutor(new MagicItemCommand());

        //Application Command
        getCommand("apply").setExecutor(new ApplicationCommand());

        //Events
        getServer().getPluginManager().registerEvents(new RandomMagics(),this);
        getServer().getPluginManager().registerEvents(new PlayerItemsListener(),this);
        getServer().getPluginManager().registerEvents(new PreventTrade(),this);

        //Logger
        getLogger().info("Your plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Your plugin has been disabled!");
    }

    //Instance Accesses
    public static SchoolSMP getPlugin() {
        return plugin;
    }
    public static String pluginMsg(){return plmsg;}
    public static WorldGuardHelper helper(){return helper;}


}
