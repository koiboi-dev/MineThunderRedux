package me.kotos.minethunder.vehicles.settings;

import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.utils.JSONUtils;
import org.bukkit.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public abstract class VehicleSettings {
    private static final HashMap<String, VehicleSettings> registry = new HashMap<>();
    private final String id;
    private final String name;
    private final String desc;
    private final int modelID;
    //private final float acceleration;
    //private final float maxSpeed;
    private final Vector displayScale;
    private final int[] hitboxSize;
    private final Vector hitboxOffset;
    /*private final float turnSpeed;
    private final float baseDrag;
    private final float turningDrag;
    private final float tiltDrag;*/

    public VehicleSettings(String id, String name, String desc, int modelID, Vector displayScale, int[] hitboxSize, Vector hitboxOffset) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.modelID = modelID;
        this.displayScale = displayScale;
        this.hitboxSize = hitboxSize;
        this.hitboxOffset = hitboxOffset;
    }

    public static void LoadVehicles(){
        registry.clear();
        File tF = new File(MineThunder.getInstance().getDataFolder()+"/vehicles");
        for (File f : Objects.requireNonNull(tF.listFiles())){
            try {
                JSONObject obj = new JSONObject(new Scanner(f).useDelimiter("\\Z").next());
                if (obj.getString("type").equals("GROUND")) {
                    registry.put(f.getName().split("\\.")[0], new GroundVehicleSettings(
                            f.getName().split("\\.")[0],
                            obj.getString("name"),
                            obj.getString("description"),
                            obj.getInt("modelID"),
                            //obj.getFloat("acceleration"),
                            //obj.getFloat("maxSpeed"),
                            JSONUtils.getVectorFromJSON(obj.getJSONArray("displayScale")),
                            JSONUtils.getIntegerArrayFromJSON(obj.getJSONArray("hitboxSize")),
                            JSONUtils.getVectorFromJSON(obj.getJSONArray("hitboxOffset")),
                            //obj.getFloat("turnSpeed"),
                            //obj.getFloat("baseDrag"),
                            //obj.getFloat("turningDrag"),
                            //obj.getFloat("tiltDrag")
                            obj.getFloat("acceleration"),
                            obj.getFloat("maxSpeed"),
                            obj.getFloat("turnSpeed"),
                            obj.getFloat("baseDrag"),
                            obj.getFloat("turningDrag"),
                            obj.getFloat("tiltDrag")));

                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                MineThunder.getInstance().getLogger().severe("\u001B[31m FAILED TO LOAD VEHICLE '"+f.getName()+"' DUE TO BAD JSON FORMATTING IN "+e.getMessage()+ "\u001B[0m");
            }
        }
    }

    public static VehicleSettings getSettings(String id){
        //System.out.println(id+" : "+registry);
        return registry.get(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getModelID() {
        return modelID;
    }

    public Vector getDisplayScale() {
        return displayScale;
    }

    public int[] getHitboxSize() {
        return hitboxSize;
    }

    public Vector getHitboxOffset() {
        return hitboxOffset;
    }
}
