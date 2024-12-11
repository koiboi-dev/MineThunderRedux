package me.kotos.minethunder.versions;

import me.kotos.minethunder.MineThunder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class VersionHandler {
    private static VersionInterface inter;
    public static void setup(){
        String version = MineThunder.getInstance().getServer().getClass().getPackage().getName().split("\\.")[3];
        System.out.println(version);
        switch (version){
            case "v1_20_R1" :
                inter = new Version_1_20_1();
                break;
            case "v1_21_R2":
                inter = new Version_1_21_3();
                break;
            default:
                MineThunder.getInstance().getLogger().severe("UNSUPPORTED VERSION.");
                MineThunder.getInstance().getServer().getPluginManager().disablePlugin(MineThunder.getInstance());
                break;
        }
    }
    public static void teleport(Entity ent, Location loc){
        inter.teleport(ent, loc);
    }
}
