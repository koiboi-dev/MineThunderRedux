package me.kotos.minethunder.damagemodel.datatypes;

public class Sphere {
    private Vect origin;
    private double radius;

    public Sphere(Vect origin, double radius) {
        this.origin = origin;
        this.radius = radius;
    }

    public boolean does_ray_intersect(Ray ray){
        Vect L = origin.clone().sub(ray.getOrigin()); // Difference between origin and ray origin
        double t_ca = L.dot(ray.getDir());
        if (t_ca < 0) {
            return false;
        }
        double d_squared = L.dot(L) - t_ca * t_ca;
        if (d_squared > radius*radius){
            return false;
        }
        double thc = Math.sqrt(radius * radius - d_squared);
        //double t0 = t_ca - thc;
        return true;
    }
}
