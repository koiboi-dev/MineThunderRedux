package me.kotos.minethunder.vehicles;

import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.enums.SeatType;
import me.kotos.minethunder.utils.DisplayUtils;
import me.kotos.minethunder.utils.JSONUtils;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.vehicles.settings.TurretSettings;
import me.kotos.minethunder.versions.VersionHandler;
import me.kotos.minethunder.weaponry.weapons.FireGroup;
import me.kotos.minethunder.weaponry.weapons.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Turret extends Seat implements Listener {
    //private final TurretSettings settings;
    private final ItemDisplay model;
    private final ItemDisplay gunModel;
    private final boolean flipped;
    private float yaw;
    private float pitch;
    private final String type;
    private final Weapon[] weapons;
    private final FireGroup[] fireGroups;

    public Turret(Vehicle parent, String name, Vector pos, String type, boolean flipped, Vector scale, SeatType[] types) {
        super(parent, pos, name, types, TurretSettings.getSettings(type).seatModelID());
        this.type = type;
        //settings = TurretSettings.getSettings(type);
        this.flipped = flipped;
        if (TurretSettings.getSettings(type).fixed()) {
            model = null;
            gunModel = null;
            getSeat().remove();
            getInter().remove();
        } else {
            model = DisplayUtils.getDisplayEntity(parent.getLoc(), TurretSettings.getSettings(type).modelID(), scale);
            gunModel = DisplayUtils.getDisplayEntity(parent.getLoc(), TurretSettings.getSettings(type).gunModelID(), scale);
        }

        File file = new File(MineThunder.getInstance().getDataFolder() + "/turrets/"+this.type+".json");
        JSONObject obj = null;
        try {
            obj = new JSONObject(new Scanner(file).useDelimiter("\\Z").next());
            weapons = new Weapon[obj.getJSONArray("weapons").length()];
            fireGroups = new FireGroup[obj.getJSONArray("fireGroups").length()];
            int loop = 0;
            List<Integer>[] groupings = new List[obj.getJSONArray("fireGroups").length()];
            for (Object object : obj.getJSONArray("weapons")) {
                if (object instanceof JSONObject jsonObject){
                    weapons[loop] = new Weapon(
                            jsonObject.getString("type"),
                            JSONUtils.getVectorFromJSON(jsonObject.getJSONArray("pos")),
                            jsonObject.optBoolean("barrelMounted", true),
                            this
                    );
                    groupings[jsonObject.getInt("fireGroup")].add(loop);
                    loop++;
                }
            }
            loop = 0;
            for (Object object : obj.getJSONArray("fireGroups")) {
                if (object instanceof JSONObject jsonObject){
                    if (loop >= 5){
                        break;
                    }
                    int[] weapons = new int[groupings[loop].size()];
                    for (int i = 0; i < weapons.length; i++) {
                        weapons[i] = groupings[loop].get(i);
                    }
                    fireGroups[loop] = new FireGroup(
                            jsonObject.getString("name"),
                            Material.getMaterial(jsonObject.optString("material", "LEVER")),
                            jsonObject.optBoolean("shellFolder", false),
                            weapons,
                            JSONUtils.getStringArrayFromJSON(jsonObject.getJSONArray("shells"))
                    );
                }
                loop++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        MineThunder.getInstance().getServer().getPluginManager().registerEvents(this, MineThunder.getInstance());
    }

    @Override
    public void tick() {
        if (TurretSettings.getSettings(type).fixed()){
            return;
        }
        super.tick();
        //float vYaw = getParent().getYaw();

        if (getSeated() != null) {
            float pYaw = getSeated().getLocation().getYaw();
            float globalYaw = getParent().getYaw() + yaw;
            float globalPitch = (float) (getParent().getPitch()*Math.cos(Math.toRadians(getParent().getYaw())) + pitch);

            //yaw -= Math.min(Math.max(globalYaw+pYaw, -settings.yawSpeed()), settings.yawSpeed());
            if (globalYaw + pYaw > 180) {
                yaw -= VectorUtils.clamp(globalYaw + pYaw - 360, -TurretSettings.getSettings(type).yawSpeed(), TurretSettings.getSettings(type).yawSpeed());
            } else if (globalYaw + pYaw < -180) {
                yaw -= VectorUtils.clamp(globalYaw + pYaw + 360, -TurretSettings.getSettings(type).yawSpeed(), TurretSettings.getSettings(type).yawSpeed());
            } else {
                yaw -= VectorUtils.clamp(globalYaw + pYaw, -TurretSettings.getSettings(type).yawSpeed(), TurretSettings.getSettings(type).yawSpeed());
            }
            pitch -= VectorUtils.clamp(globalPitch - getSeated().getLocation().getPitch(), -TurretSettings.getSettings(type).pitchSpeed(), TurretSettings.getSettings(type).pitchSpeed());
            //getSeated().sendMessage(globalYaw + ": " + globalPitch + " | " + (globalYaw + pYaw) + " : " + (globalPitch + getSeated().getLocation().getPitch()));
        }
        if (yaw >= 180) {
            yaw -= 360;
        } else if (yaw <= -180) {
            yaw += 360;
        }
        if (flipped){
            yaw = VectorUtils.clamp(yaw, -TurretSettings.getSettings(type).yawRange()[1], -TurretSettings.getSettings(type).yawRange()[0]);
        } else {
            yaw = VectorUtils.clamp(yaw, TurretSettings.getSettings(type).yawRange()[0], TurretSettings.getSettings(type).yawRange()[1]);
        }
        pitch = VectorUtils.clamp(pitch, TurretSettings.getSettings(type).pitchRange()[0], TurretSettings.getSettings(type).pitchRange()[1]);
        /*if (getSeated() != null) {
            float pYaw = VectorUtils.getYawDifference(vYaw, getSeated().getLocation().getYaw()); //vYaw+getSeated().getLocation().getYaw();

            if (yaw > pYaw) {
                if (yaw > 90 && pYaw < -90) {
                    yaw += settings.yawSpeed();
                } else yaw -= settings.yawSpeed();
            } else if (yaw < pYaw) {
                if (yaw < -90 && pYaw > 90) {
                    yaw -= settings.yawSpeed();
                } else yaw += settings.yawSpeed();
            }
            if (yaw < pYaw + settings.yawSpeed() && yaw > pYaw - settings.yawSpeed()) {
                yaw = pYaw;
            }

            float pPitch = getParent().getPitch()-getSeated().getLocation().getPitch();
            if (pitch < pPitch) {
                pitch += settings.pitchSpeed();
            } else if (pitch > pPitch) {
                pitch -= settings.pitchSpeed();
            }

            if (pitch > pPitch - settings.pitchSpeed() && pitch < pPitch + settings.pitchSpeed()) {
                pitch = pPitch;
            }
            getSeated().sendMessage(yaw + ": "+ pitch + " | "+ pYaw+ " : "+ pPitch);
            // Add nice components or smth idk
        }
        if (flipped){
            yaw = VectorUtils.clamp(yaw, -settings.yawRange()[1], -settings.yawRange()[0]);
        } else {
            yaw = VectorUtils.clamp(yaw, settings.yawRange()[0], settings.yawRange()[1]);
        }*/
    }

    @Override
    public void teleport() {
        if (TurretSettings.getSettings(type).fixed()){
            return;
        }
        Vector nV = getPos().clone();
        Vector seatPos = TurretSettings.getSettings(type).seatPos().clone();
        //nV.rotateAroundZ(Math.toRadians(roll)).rotateAroundX(Math.toRadians(pitch)).rotateAroundY(Math.toRadians(yaw));
        nV = VectorUtils.rotateVectorByEuler(nV, getParent().getRoll(), getParent().getPitch(), getParent().getYaw());
        seatPos = VectorUtils.rotateVectorByEuler(seatPos, getParent().getRoll(), getParent().getPitch(), getParent().getYaw()+this.yaw);
        float globalPitch = (float) (getParent().getPitch() * Math.sin(Math.toRadians(this.yaw)) + getParent().getRoll() * Math.cos(Math.toRadians(this.yaw)));
        DisplayUtils.moveDisplayEntity(
                gunModel,
                getParent().getLoc().add(nV),
                globalPitch,
                (float) (this.pitch + (getParent().getPitch() * Math.cos(Math.toRadians(this.yaw)) - getParent().getRoll() * Math.sin(Math.toRadians(this.yaw)))),
                getParent().getYaw() + this.yaw
        );
        DisplayUtils.moveDisplayEntity(
                model,
                getParent().getLoc().add(nV),
                globalPitch,
                (float) ((getParent().getPitch() * Math.cos(Math.toRadians(this.yaw)) - getParent().getRoll() * Math.sin(Math.toRadians(this.yaw)))),
                getParent().getYaw() + this.yaw
        );
        VersionHandler.teleport(getInter(), getParent().getLoc().add(nV));

        DisplayUtils.moveDisplayEntity(getSeat(), getParent().getLoc().add(nV).add(seatPos), getParent().getRoll(), getParent().getPitch(), getParent().getYaw());

        //model.setRotation(yaw+this.yaw, pitch+this.pitch);
    }

    @Override
    public void remove() {
        super.remove();
        HandlerList.unregisterAll(this);
    }


    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e){
        if (getSeated() == null || e.getPlayer() != getSeated()){
            return;
        }
        e.setCancelled(true);

    }


    public float getYaw() {
        return yaw;
    }
    public float getGlobalYaw(){
        return getParent().getYaw()+getYaw();
    }

    public float getGlobalPitch(){
        return (float) (this.pitch + (getParent().getPitch() * Math.cos(Math.toRadians(this.yaw)) - getParent().getRoll() * Math.sin(Math.toRadians(this.yaw))));
    }

    public float getGlobalRoll(){
        return (float) (getParent().getPitch() * Math.sin(Math.toRadians(getParent().getYaw())) + pitch * Math.sin(Math.toRadians(getParent().getYaw())));
    }

    public float getPitch() {
        return pitch;
    }

    public TurretSettings getSettings(){
        return TurretSettings.getSettings(type);
    }

    @Override
    public PlayerInventory generateDefaultLayout() {
        PlayerInventory temp = (PlayerInventory) Bukkit.createInventory(null, InventoryType.PLAYER);
        ItemStack[] contents = new ItemStack[temp.getContents().length];

        int loop = 0;
        for (FireGroup g : fireGroups) {
            ItemStack stack = new ItemStack(Material.getMaterial(g.name()), 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + g.name());
            stack.setItemMeta(meta);
            contents[loop] = stack;
            loop++;
        }

        temp.setContents(contents);
        return temp;
    }
}
