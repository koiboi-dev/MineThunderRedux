package me.kotos.minethunder.vehicles;

import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.damagemodel.DamageModel;
import me.kotos.minethunder.enums.SeatType;
import me.kotos.minethunder.utils.DisplayUtils;
import me.kotos.minethunder.utils.JSONUtils;
import me.kotos.minethunder.vehicles.settings.VehicleSettings;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class Vehicle {
    private Location loc;
    private float roll;
    private float pitch;
    private float yaw;
    private final ItemDisplay model;
    private final String id;
    //private VehicleSettings settings;
    private final List<Player> seated = new ArrayList<>();

    //private final Turret[] turrets;
    private final Seat[] seats;
    private float xInput;
    private float yInput;
    private final DamageModel damageModel;

    public Vehicle(Location loc, String id){
        this.loc = loc;
        this.id = id;
        //this.settings = VehicleSettings.getSettings(id);
        try {
            File file = new File(MineThunder.getInstance().getDataFolder() + "/vehicles/"+getId()+".json");
            JSONObject obj = new JSONObject(new Scanner(file).useDelimiter("\\Z").next());

            // Load Seats
            seats = new Seat[obj.getJSONArray("seats").length()+obj.getJSONArray("turrets").length()];
            int loops = 0;
            for (Object sObj : obj.getJSONArray("seats")){
                if (sObj instanceof JSONObject seat) {
                    seats[loops] = new Seat(
                            this,
                            JSONUtils.getVectorFromJSON(seat.getJSONArray("pos")),
                            seat.getString("name"),
                            JSONUtils.getSeatTypesFromJSON(seat.getJSONArray("roles")),
                            seat.optInt("seatModelID", -1)
                    );
                    loops++;
                } else {
                    MineThunder.getInstance().getLogger().severe("BAD SEAT TYPE IN " + obj.getString("name"));
                }
            }

            for (Object tObj : obj.getJSONArray("turrets")) {
                if (tObj instanceof JSONObject turret) {
                    SeatType[] types;
                    if (turret.has("roles")) {
                        SeatType[] extras = JSONUtils.getSeatTypesFromJSON(turret.getJSONArray("roles"));
                        types = new SeatType[extras.length+1];
                        types[0] = SeatType.GUNNER;
                        System.arraycopy(extras, 0, types, 1, extras.length);
                        //types.addAll(List.of(JSONUtils.getSeatTypesFromJSON(turret.getJSONArray("roles"))));
                    } else {
                        types = new SeatType[] {SeatType.GUNNER};
                    }
                    seats[loops] = new Turret(
                            this,
                            turret.getString("name"),
                            JSONUtils.getVectorFromJSON(turret.getJSONArray("pos")),
                            turret.getString("type"),
                            turret.optBoolean("flipped"),
                            VehicleSettings.getSettings(id).getDisplayScale(),
                            types
                    );
                    loops++;
                } else {
                    MineThunder.getInstance().getLogger().severe("BAD TURRET TYPE IN " + obj.getString("name"));
                }
            }
            damageModel = new DamageModel(this, obj.optJSONObject("model", new JSONObject()));
            model = DisplayUtils.getDisplayEntity(loc, VehicleSettings.getSettings(id).getModelID(), VehicleSettings.getSettings(id).getDisplayScale());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSettings(){
        //this.settings = VehicleSettings.getSettings(id);
    }

    public void updateSeats(){
        for (Seat seat : seats){
            seat.teleport();
            seat.tick();
        }
    }

    /*public void updateTurrets(float roll, float pitch, float yaw){
        for (Turret turret : turrets){
            turret.teleport(loc, roll, pitch, yaw);
            turret.tick();
        }
    }*/

    public Seat getPlayerSeatInVehicle(Player p){
        for (Seat s : seats){
            if (s.getSeated() == p){
                return s;
            }
        }
        return null;
    }

    public boolean isPlayerInVehicle(Player p) {
        return seated.contains(p);
    }

    public Location getLoc() {
        return loc.clone();
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }
    public void setLoc(double x, double y, double z) {
        this.loc.setX(x);
        this.loc.setY(y);
        this.loc.setZ(z);
    }

    public String getId() {
        return id;
    }

    public ItemDisplay getModel() {
        return model;
    }

    public Seat[] getSeats() {
        return seats;
    }

    public List<Player> getSeated() {
        return seated;
    }

    public VehicleSettings getSettings() {
        return VehicleSettings.getSettings(id);
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getXInput() {
        return xInput;
    }

    public void setXInput(float xInput) {
        this.xInput = xInput;
    }

    public float getYInput() {
        return yInput;
    }

    public void setYInput(float yInput) {
        this.yInput = yInput;
    }
}
