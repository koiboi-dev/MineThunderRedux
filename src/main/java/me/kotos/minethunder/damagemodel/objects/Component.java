package me.kotos.minethunder.damagemodel.objects;


import me.kotos.minethunder.damagemodel.datatypes.CollisionData;
import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.Vect;

public class Component extends AABB implements DamageObject {
    private float health;
    private final float maxHealth;
    public Component(Vect pos, Vect size, float maxHealth) {
        super(pos, size);
        this.maxHealth = maxHealth;
    }

    @Override
    public CollisionData collideRayWithSelf(Ray ray) {
        return new CollisionData(collideRay(ray), ray.getDir(), this);
    }
    @Override
    public int getTurretID(){
        return -1;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }
}
