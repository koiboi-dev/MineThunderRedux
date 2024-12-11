package me.kotos.minethunder.vehicles.settings;

import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.utils.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public record WeaponSettings(int clipSize, int maxReserveAmmo, int fireSpeed, int reloadTime, int loaderReloadTime, String[] shells, float shellSpeed) {
    private static final HashMap<String, WeaponSettings> registry = new HashMap<>();

    public static void LoadWeapons(){
        File tF = new File(MineThunder.getInstance().getDataFolder()+"/turrets");
        for (File f : Objects.requireNonNull(tF.listFiles())){
            try {
                JSONObject obj = new JSONObject(new Scanner(f).useDelimiter("\\Z").next());
                registry.put(f.getName().split("\\.")[0] ,new WeaponSettings(
                        obj.getInt("clipSize"),
                        obj.getInt("maxReserveAmmo"),
                        obj.getInt("fireSpeed"),
                        obj.getInt("reloadTime"),
                        obj.getInt("loaderReloadTime"),
                        JSONUtils.getStringArrayFromJSON(obj.getJSONArray("shells")),
                        obj.getFloat("shellSpeed")
                ));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                MineThunder.getInstance().getLogger().severe("\u001B[31m FAILED TO LOAD SHELL '"+f.getName()+"' DUE TO "+e.getMessage()+"\u001B[0m");
            }
        }
    }

    public static WeaponSettings getSettings(String type) {
        return registry.get(type);
    }
}
