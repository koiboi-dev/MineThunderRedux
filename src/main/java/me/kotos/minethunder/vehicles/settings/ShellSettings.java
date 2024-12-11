package me.kotos.minethunder.vehicles.settings;

import me.kotos.minethunder.MineThunder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public record ShellSettings(String name, String fullName, int tntPower, int penetration, int damage, int modelId) {
    private static final HashMap<String, ShellSettings> registry = new HashMap<>();

    public static void LoadShells(){
        File tF = new File(MineThunder.getInstance().getDataFolder()+"/shells");
        for (File f : Objects.requireNonNull(tF.listFiles())){
            try {
                JSONObject obj = new JSONObject(new Scanner(f).useDelimiter("\\Z").next());
                registry.put(f.getName().split("\\.")[0], new ShellSettings(
                        obj.getString("name"),
                        obj.getString("fullName"),
                        obj.optInt("tntPower", 1),
                        obj.getInt("penetration"),
                        obj.getInt("damage"),
                        obj.optInt("modelId")
                ));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                MineThunder.getInstance().getLogger().severe("\u001B[31m FAILED TO LOAD SHELL '"+f.getName()+"' DUE TO "+e.getMessage()+"\u001B[0m");
            }
        }
    }

    public static ShellSettings getShell(String shell){
        return registry.get(shell);
    }
}
