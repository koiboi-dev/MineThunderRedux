package me.kotos.minethunder.vehicles.settings;

import org.bukkit.util.Vector;

public final class GroundVehicleSettings extends VehicleSettings {
    private final float acceleration;
    private final float maxSpeed;
    private final float turnSpeed;
    private final float baseDrag;
    private final float turningDrag;
    private final float tiltDrag;
    public GroundVehicleSettings(String id, String name, String desc, int modelID, Vector displayScale, int[] hitboxSize, Vector hitboxOffset, float acceleration, float maxSpeed, float turnSpeed, float baseDrag, float turningDrag, float tiltDrag) {
        super(id, name, desc, modelID, displayScale, hitboxSize, hitboxOffset);
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
        this.turnSpeed = turnSpeed;
        this.baseDrag = baseDrag;
        this.turningDrag = turningDrag;
        this.tiltDrag = tiltDrag;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getTurnSpeed() {
        return turnSpeed;
    }

    public float getBaseDrag() {
        return baseDrag;
    }

    public float getTurningDrag() {
        return turningDrag;
    }

    public float getTiltDrag() {
        return tiltDrag;
    }
}
