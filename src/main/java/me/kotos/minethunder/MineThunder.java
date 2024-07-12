package me.kotos.minethunder;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.kotos.minethunder.updaters.Updater;
import me.kotos.minethunder.vehicles.settings.ShellSettings;
import me.kotos.minethunder.vehicles.settings.TurretSettings;
import me.kotos.minethunder.vehicles.settings.VehicleSettings;
import me.kotos.minethunder.versions.VersionHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class MineThunder extends JavaPlugin {
    private static MineThunder plugin;
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        getCommand("minethunder").setExecutor(new CommandHandler());
        getServer().getPluginManager().registerEvents(new EventHandler(), this);

        getLogger().info("Setting Version Handler...");
        VersionHandler.setup();

        getLogger().info("Loading Configs...");
        saveDefaultConfig();

        File files = new File(MineThunder.getInstance().getDataFolder()+"/vehicles");
        File shells = new File(MineThunder.getInstance().getDataFolder()+"/shells");
        File turret = new File(MineThunder.getInstance().getDataFolder()+"/turrets");
        if (!files.exists()){
            files.mkdir();
            MineThunder.getInstance().saveResource("vehicles/Mk4.json", false);
            MineThunder.getInstance().saveResource("vehicles/Challenger.json", false);
        }
        if (!shells.exists()){
            shells.mkdir();
            MineThunder.getInstance().saveResource("shells/HESH.json", false);
            MineThunder.getInstance().saveResource("shells/WWI_HE.json", false);
        }
        if (!turret.exists()){
            turret.mkdir();
            MineThunder.getInstance().saveResource("turrets/ChallengerTurret.json", false);
            MineThunder.getInstance().saveResource("turrets/Mk4Turret.json", false);
        }

        getLogger().info("Loading Shells...");
        ShellSettings.LoadShells();
        getLogger().info("Loading Turrets...");
        TurretSettings.LoadTurrets();
        getLogger().info("Loading Vehicles...");
        VehicleSettings.LoadVehicles();

        getLogger().info("Starting updater...");
        new BukkitRunnable() {
            @Override
            public void run() {
                Updater.tickAll();
            }
        }.runTaskTimer(this, 10, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MineThunder getInstance(){
        return plugin;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
