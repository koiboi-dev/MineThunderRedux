package me.kotos.minethunder.damagemodel;

import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.Sphere;
import me.kotos.minethunder.damagemodel.datatypes.Vect;
import me.kotos.minethunder.damagemodel.objects.CollisionType;
import me.kotos.minethunder.damagemodel.objects.DamageObject;
import me.kotos.minethunder.damagemodel.objects.Panel;

public class Main {
    public static void main(String[] args) {
        Sphere sphere = new Sphere(new Vect(0, 0, 0), 8);
        System.out.println(sphere.does_ray_intersect(new Ray(new Vect(-10, 0, 0), new Vect(0.5,0.5,0))));
    }
}
