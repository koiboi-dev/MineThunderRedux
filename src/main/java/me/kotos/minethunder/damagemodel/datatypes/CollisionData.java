package me.kotos.minethunder.damagemodel.datatypes;

import me.kotos.minethunder.damagemodel.objects.Component;
import me.kotos.minethunder.damagemodel.objects.DamageObject;
import me.kotos.minethunder.damagemodel.objects.Panel;

public record CollisionData(Vect point, Vect dir, DamageObject damageObject) {
    public Vect getNormal(){
        if (damageObject instanceof Panel plane) {
            if (dir.dot(plane.getNormal()) < 0) {
                return plane.getNormal();
            } else {
                return plane.getNormal().getInverted();
            }
        }
        return dir.clone();
    }
    public Ray getOutRay(float thickness){
        if (damageObject instanceof Panel plane) {
            Vect normal = getNormal();
            System.out.println(normal.dot(dir)+" : "+(1-Math.abs(normal.dot(dir)))+" : "+ (plane.getToughness()/thickness)+ " : "+thickness+ " : "+((1-Math.abs(normal.dot(dir)))*(plane.getToughness()/thickness)));
            return new Ray(point.clone().add(dir.clone().scale(0.000001f)), dir.clone().add(normal.scale((1-Math.abs(normal.dot(dir)))*0.25)));
        }
        return new Ray(point.clone().add(dir.clone().scale(0.000001f)), dir);
    }
    public float getImpactThickness() {
        if (damageObject instanceof Component c) {
            return c.getHealth();
        } else if (damageObject instanceof Panel plane) {
            Vect normal = getNormal();
            Vect offset = normal.clone().scale(plane.getThickness()*0.001);
            TriPlane triPlane = new TriPlane(plane.getS1().add(offset), plane.getS2().add(offset), plane.getS3().add(offset), plane.getType());
            return triPlane.intersectRayWithPlane(new Ray(point, dir)).distance(point)*1000;
        }
        return 0;
    }
    public void applyPenetrationEnergy(float penLeft){
        if (damageObject instanceof Component c) {
            c.setHealth((float) (c.getHealth() - (penLeft * 0.25)));
        }
    }
}
