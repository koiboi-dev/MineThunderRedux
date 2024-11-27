package me.kotos.minethunder.vehicles;

import me.kotos.minethunder.enums.SeatType;
import me.kotos.minethunder.utils.DisplayUtils;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.vehicles.settings.TurretSettings;
import me.kotos.minethunder.versions.VersionHandler;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Turret extends Seat {
    //private final TurretSettings settings;
    private final ItemDisplay model;
    private final ItemDisplay gunModel;
    private final boolean flipped;
    private float yaw;
    private float pitch;
    private final String type;

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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
