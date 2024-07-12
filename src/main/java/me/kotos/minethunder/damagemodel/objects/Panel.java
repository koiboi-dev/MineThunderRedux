package me.kotos.minethunder.damagemodel.objects;

import me.kotos.minethunder.damagemodel.datatypes.CollisionData;
import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.Vect;

public class Panel extends TriPlane implements DamageObject {
    private final float toughness;
    private final float thickness;
    private final int turretID;

    public Panel(Vect s1, Vect s2, Vect s3, CollisionType type, float thickness, float toughness, int turretID) {
        super(s1, s2, s3, type);
        this.thickness = thickness;
        this.toughness = toughness;
        this.turretID = turretID;
    }

    @Override
    public CollisionData collideRayWithSelf(Ray ray) {
        Vect pos = intersectRayWithSelf(ray);
        if (pos == null) return null;
        return new CollisionData(pos, ray.getDir(),this);
    }

    @Override
    public int getTurretID() {
        return turretID;
    }

    public float getThickness() {
        return thickness;
    }

    public float getToughness() {
        return toughness;
    }
}
