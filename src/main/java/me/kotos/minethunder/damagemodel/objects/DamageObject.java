package me.kotos.minethunder.damagemodel.objects;

import me.kotos.minethunder.damagemodel.datatypes.CollisionData;
import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.Vect;

public interface DamageObject {
    CollisionData collideRayWithSelf(Ray ray);
    int getTurretID();
}
