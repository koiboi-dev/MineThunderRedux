package me.kotos.minethunder.vehicles.settings;

import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.utils.JSONUtils;
import me.kotos.minethunder.weaponry.weapons.FireGroup;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public record TurretSettings(float[] pitchRange, float[] yawRange, boolean remote, int modelID, int gunModelID, float pitchSpeed, float yawSpeed, boolean fixed, int seatModelID, Vector seatPos) {

    private static final HashMap<String, TurretSettings> registry = new HashMap<>();

    public static void LoadTurrets(){
        File tF = new File(MineThunder.getInstance().getDataFolder()+"/turrets");
        for (File f : Objects.requireNonNull(tF.listFiles())){
            try {
                JSONObject obj = new JSONObject(new Scanner(f).useDelimiter("\\Z").next());
                //String[] shells = new String[obj.getJSONArray("shells").length()];
                //int loop = 0;
                //for (Object hm : obj.getJSONArray("shells")){
                //    shells[loop] = (String) hm;
                //    loop++;
                //}

                registry.put(f.getName().split("\\.")[0], new TurretSettings(
                        new float[] {obj.getJSONArray("pitchRange").getFloat(0), obj.getJSONArray("pitchRange").getFloat(1)},
                        new float[] {obj.getJSONArray("yawRange").getFloat(0), obj.getJSONArray("yawRange").getFloat(1)},
                        obj.optBoolean("remote"),
                        obj.getInt("modelID"),
                        obj.optInt("gunModelID", -1),
                        obj.getFloat("pitchSpeed"),
                        obj.getFloat("yawSpeed"),
                        obj.optBoolean("fixed"),
                        obj.optInt("seatModelID", -1),
                        JSONUtils.getVectorFromJSON(obj.getJSONArray("seatPos"))
                ));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                MineThunder.getInstance().getLogger().severe("\u001B[31m FAILED TO LOAD TURRET '"+f.getName()+"' DUE TO "+e.getMessage()+"\u001B[0m");
            }
        }
    }

    public static TurretSettings getSettings(String type){
        return registry.get(type);
    }
}
