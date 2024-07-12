package me.kotos.minethunder.damagemodel;

import me.kotos.minethunder.damagemodel.datatypes.CollisionData;
import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.Vect;
import me.kotos.minethunder.damagemodel.objects.CollisionType;
import me.kotos.minethunder.damagemodel.objects.Component;
import me.kotos.minethunder.damagemodel.objects.DamageObject;
import me.kotos.minethunder.damagemodel.objects.Panel;
import me.kotos.minethunder.utils.JSONUtils;
import me.kotos.minethunder.vehicles.Turret;
import me.kotos.minethunder.vehicles.Vehicle;
import me.kotos.minethunder.damagemodel.datatypes.ShellData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageModel {
    private DamageObject[] damageObjects;
    private final Vehicle parent;
    public static final Vect SHRAPNEL_DEVATION_BOX_MIN = new Vect(-0.5, -0.5, -0.5);
    public static final Vect SHRAPNEL_DEVATION_BOX_MAX = new Vect(0.5, 0.5, 1);
    public DamageModel(Vehicle parent, JSONObject modelJSON) {
        this.parent = parent;
        JSONArray ary = modelJSON.getJSONArray("objects");
        damageObjects = new DamageObject[ary.length()];
        for (int i = 0; i < ary.length(); i++) {
            if (ary.get(i) instanceof JSONObject data) {
                if (Objects.equals(data.getString("dtype"), "ARMOUR")) {
                    damageObjects[i] = new Panel(
                            JSONUtils.getVectFromJSON(data.getJSONArray("S1")),
                            JSONUtils.getVectFromJSON(data.getJSONArray("S2")),
                            JSONUtils.getVectFromJSON(data.getJSONArray("S3")),
                            CollisionType.valueOf(data.optString("types")),
                            data.getFloat("thickness"),
                            data.getFloat("toughness"),
                            data.getInt("turret")
                    );
                } else if (Objects.equals(data.getString("dtype"), "COMP")) {
                    damageObjects[i] = new Component(
                            JSONUtils.getVectFromJSON(data.getJSONArray("pos")),
                            JSONUtils.getVectFromJSON(data.getJSONArray("size")),
                            data.optFloat("health", 1000)
                    );
                }
            }
        }
    }
    public DamageModel(Vehicle parent, DamageObject[] objs){
        this.parent = parent;
        this.damageObjects = objs;
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
}
