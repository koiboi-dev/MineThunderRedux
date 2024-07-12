package me.kotos.minethunder.damagemodel.objects;


import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.Vect;

public class AABB {
    private final Vect minV;
    private final Vect maxV;

    public AABB(Vect pos, Vect size) {
        minV = pos.clone().sub(size.clone().scale(0.5f));
        maxV = pos.clone().add(size.clone().scale(0.5f));
    }
    public Vect collideRay(Ray ray) {
        double t1 = (minV.getX() - ray.getOrigin().getX()) / ray.getDir().getX();
        double t2 = (maxV.getX() - ray.getOrigin().getX()) / ray.getDir().getX();
        double t3 = (minV.getY() - ray.getOrigin().getY()) / ray.getDir().getY();
        double t4 = (maxV.getY() - ray.getOrigin().getY()) / ray.getDir().getY();
        double t5 = (minV.getZ() - ray.getOrigin().getZ()) / ray.getDir().getZ();
        double t6 = (maxV.getZ() - ray.getOrigin().getZ()) / ray.getDir().getZ();

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.max(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
        if (tmax < 0) {
            return null;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax) {
            return null;
        }
        if (tmin < 0f) {
            if (!pointIsInsideBox(ray.getOrigin().add(ray.getDir().scale(tmax)))){
                return null;
            }
            return ray.getOrigin().add(ray.getDir().scale(tmax));
        }
        if (!pointIsInsideBox(ray.getOrigin().add(ray.getDir().scale(tmin)))){
            return null;
        }
        return ray.getOrigin().add(ray.getDir().scale(tmin));
    }
    public boolean pointIsInsideBox(Vect point){
        return minV.getX() <= point.getX() && point.getX() <= maxV.getX() &&
                minV.getY() <= point.getY() && point.getY() <= maxV.getY() &&
                minV.getZ() <= point.getZ() && point.getZ() <= maxV.getZ();
    }
}
