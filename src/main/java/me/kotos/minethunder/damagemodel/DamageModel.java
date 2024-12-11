package me.kotos.minethunder.damagemodel;

import me.kotos.minethunder.damagemodel.datatypes.*;
import me.kotos.minethunder.damagemodel.objects.CollisionType;
import me.kotos.minethunder.damagemodel.objects.Component;
import me.kotos.minethunder.damagemodel.objects.DamageObject;
import me.kotos.minethunder.damagemodel.objects.Panel;
import me.kotos.minethunder.utils.JSONUtils;
import me.kotos.minethunder.vehicles.Turret;
import me.kotos.minethunder.vehicles.Vehicle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageModel {
    private DamageObject[] damageObjects;
    private final Vehicle parent;
    private final AABB hitbox;

    public static final Vect SHRAPNEL_DEVATION_BOX_MIN = new Vect(-0.5, -0.5, -0.5);
    public static final Vect SHRAPNEL_DEVATION_BOX_MAX = new Vect(0.5, 0.5, 1);

    public DamageModel(Vehicle parent, JSONObject modelJSON) {
        this.parent = parent;
        if (modelJSON != null) {
            JSONArray ary = modelJSON.getJSONArray("objects");
            this.damageObjects = new DamageObject[ary.length()];
            for (int i = 0; i < ary.length(); i++) {
                if (ary.get(i) instanceof JSONObject data) {
                    if (Objects.equals(data.getString("dtype"), "ARMOUR")) {
                        this.damageObjects[i] = new Panel(
                                JSONUtils.getVectFromJSON(data.getJSONArray("S1")),
                                JSONUtils.getVectFromJSON(data.getJSONArray("S2")),
                                JSONUtils.getVectFromJSON(data.getJSONArray("S3")),
                                CollisionType.valueOf(data.optString("type", "SQUARE")),
                                data.getFloat("thickness"),
                                data.getFloat("toughness"),
                                data.getInt("turret")
                        );
                    } else if (Objects.equals(data.getString("dtype"), "COMP")) {
                        this.damageObjects[i] = new Component(
                                JSONUtils.getVectFromJSON(data.getJSONArray("pos")),
                                JSONUtils.getVectFromJSON(data.getJSONArray("size")),
                                data.optFloat("health", 1000)
                        );
                    }
                }
            }
            this.hitbox = calculateBounds();
        } else {
            this.hitbox = new AABB(new Vect(0,0,0), new Vect(0,0,0));
        }
    }
    public DamageModel(Vehicle parent, DamageObject[] objs, AABB hitbox){
        this.parent = parent;
        this.damageObjects = objs;
        this.hitbox = hitbox;
    }

    public AABB calculateBounds() {
        Vect minV = new Vect(0,0,0);
        Vect maxV = new Vect(0,0,0);

        for (DamageObject obj : this.damageObjects) {
            if (obj instanceof Panel p) {
                minV = minV.min(p.getS1());
                minV = minV.min(p.getS2());
                minV = minV.min(p.getS3());
                maxV = maxV.min(p.getS1());
                maxV = maxV.min(p.getS2());
                maxV = maxV.min(p.getS3());
            } else if (obj instanceof Component comp) {
                minV = minV.min(comp.getMinV());
                maxV = maxV.max(comp.getMaxV());
            }
        }
        AABB aabb = new AABB(new Vect(0,0,0), new Vect(0,0,0));
        aabb.setMinV(minV);
        aabb.setMaxV(maxV);
        return aabb;
    }

    public boolean doesRayIntersectBox(Ray ray){
        return hitbox.collideRay(getRayInverseTransform(ray, new Vect(parent.getLoc().toVector()), parent.getRoll(), parent.getPitch(), parent.getYaw())) != null;
    }

    public Ray getRayInverseTransform(Ray ray, Vect point, float roll, float pitch, float yaw) {
        return ray.clone().rotateRayAroundPoint(point, roll, pitch, yaw);
    }
    public CollisionData[] getCollisionPoints(Ray ray) {
        List<CollisionData> points = new ArrayList<>();
        for (DamageObject damageObject : damageObjects) {
            CollisionData m;
            if (damageObject.getTurretID() != -1 && parent.getSeats()[damageObject.getTurretID()] instanceof Turret t) {
                m = damageObject.collideRayWithSelf(getRayInverseTransform(
                        ray,
                        new Vect(t.getPos()),
                        parent.getRoll(),
                        t.getPitch() + parent.getPitch(),
                        t.getYaw() + parent.getYaw()
                ));
            } else {
                m = damageObject.collideRayWithSelf(ray);
            }
            if (m != null) {
                points.add(m);
            }
        }
        return points.toArray(new CollisionData[]{});
    }
    public CollisionData getFirstCollisionPoint(Ray ray) {
        CollisionData[] points = getCollisionPoints(ray);
        float lowestDist = Float.MAX_VALUE;
        CollisionData out = null;
        for (CollisionData v : points){
            System.out.println(v);
            float dist = v.point().distanceSquared(ray.getOrigin());
            if (dist < lowestDist){
                lowestDist = dist;
                out = v;
            }
        }
        return out;
    }
    public void calculateShrapnel(CollisionData data, float penNeeded){
        //System.out.println((penNeeded*0.25));
        for (int i = 0; i < Math.min(Math.floor((((Panel) data.damageObject()).getThickness()-penNeeded)/10), ((Panel) data.damageObject()).getThickness()/4); i++) {
            Ray ray = data.getOutRay(penNeeded);
            ray.setDir(ray.getDir().add(Vect.getRandomVectInBox(SHRAPNEL_DEVATION_BOX_MIN, SHRAPNEL_DEVATION_BOX_MAX)));
            calculateProjectileStrike(ray, 20, false);
        }
    }
    public void calculateProjectileStrike(Ray proj, float penLeft, boolean produceShrapnel) {
        System.out.println("=== "+proj.getOrigin()+" : "+proj.getDir());
        CollisionData collisionPoint = getFirstCollisionPoint(proj);
        if (collisionPoint == null){
            return;
        }
        collisionPoint.applyPenetrationEnergy(penLeft);
        float penNeeded = collisionPoint.getImpactThickness();
        System.out.println(penNeeded);
        penLeft -= penNeeded;
        System.out.println(collisionPoint.getNormal().getInverted().angle(proj.getDir())+" - "+Math.toDegrees(collisionPoint.getNormal().getInverted().angle(proj.getDir())));
        if (penLeft <= 0){
            if (Math.toDegrees(collisionPoint.getNormal().getInverted().angle(proj.getDir())) > 67.5){
                proj.setDir(proj.getDir().reflect(collisionPoint.getNormal()));
                proj.setOrigin(collisionPoint.point().clone().add(proj.getDir().scale(0.0001f)));
                System.out.println(proj.getOrigin()+" | "+proj.getDir()+ " | "+collisionPoint.point());
                penLeft += penNeeded/2;
                calculateProjectileStrike(proj, penLeft, produceShrapnel);
            }
            return;
        }
        if (produceShrapnel && collisionPoint.damageObject() instanceof Panel) {
            calculateShrapnel(collisionPoint, penNeeded);
        }
        calculateProjectileStrike(collisionPoint.getOutRay(penNeeded), penLeft, produceShrapnel);
    }

    public void renderComponents() {
        DamageObject[] var1 = this.damageObjects;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DamageObject obj = var1[var3];
            if (obj instanceof TriPlane tri) {
                debugParticle((Location)this.parent.getLoc().add(tri.getS1().toVector()), Color.BLUE, 3);
                debugParticle((Location)this.parent.getLoc().add(tri.getS2().toVector()), Color.BLUE, 3);
                debugParticle((Location)this.parent.getLoc().add(tri.getS3().toVector()), Color.BLUE, 3);
            } else if (obj instanceof Component comp) {
                debugParticle((Location)this.parent.getLoc().add(comp.getMinV().toVector()), Color.ORANGE, 3);
                debugParticle((Location)this.parent.getLoc().add(comp.getMaxV().toVector()), Color.ORANGE, 3);
            }
        }

    }

    public static void debugLine(Location startLoc, Location endLoc, float interval, Color color, int size) {
        float distance = (float)startLoc.distance(endLoc);
        Vector dir = endLoc.toVector().subtract(endLoc.toVector()).normalize();

        for(int i = 0; (double)i < Math.floor((double)(distance / interval)); ++i) {
            debugParticle(startLoc.add(dir), color, size);
        }

    }

    public void debugParticle(Vector loc, Color color, int size) {
        debugParticle(loc.toLocation(this.parent.getLoc().getWorld()), color, size);
    }

    public static void debugParticle(Location loc, Color color, int size) {
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(color, (float)size));
    }
}
