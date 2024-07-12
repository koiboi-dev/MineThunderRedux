package me.kotos.minethunder.damagemodel;

import me.kotos.minethunder.damagemodel.datatypes.CollisionData;
import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.ShellData;
import me.kotos.minethunder.damagemodel.datatypes.Vect;
import me.kotos.minethunder.damagemodel.objects.CollisionType;
import me.kotos.minethunder.damagemodel.objects.DamageObject;
import me.kotos.minethunder.damagemodel.objects.Panel;
import me.kotos.minethunder.damagemodel.objects.TriPlane;

public class Main {
    public static final Vect min = new Vect(-2, -2, -2);
    public static final Vect max = new Vect(2, 2, 2);
    public static final int PANELCOUNT = 128;
    public static void main(String[] args) {
        // Panel panel = new Panel(new Vect(-2, -2, 0), new Vect(-2, 2, -1), new Vect(2, -2, 0), CollisionType.TRI, 20, 20, -1);
        //System.out.println(panel.collideRayWithSelf(new Ray(new Vect(0, 0, -3), new Vect(0, 0, -1))));
        // AABB aabb = new AABB(new Vect(0, 0, 0), new Vect(1, 1, 1));
        DamageModel model = new DamageModel(null, new DamageObject[] {new Panel(new Vect(0, 0, 0), new Vect(1, 0, 0),new Vect(0, 0, 1), CollisionType.TRI,200, 500, -1)});
        model.calculateProjectileStrike(new Ray(new Vect(0.5, 0.25, 0.5), new Vect(0, -0.25, -0.4)), 400, false);

        /*System.out.println("WHAT");
        DamageObject[] objs = new DamageObject[PANELCOUNT];
        System.out.println("Loading...");
        for (int i = 0; i < PANELCOUNT; i++) {
            objs[i] = new Panel(
                    Vect.getRandomVectInBox(min, max),
                    Vect.getRandomVectInBox(min, max),
                    Vect.getRandomVectInBox(min, max),
                    CollisionType.TRI,
                    250,
                    1,
                    -1
            );
        }
        System.out.println("Colliding...");
        ShellData sD = new ShellData("AFB", "ahmads fat balls", 200, 400, 0, 5000, true);
        DamageModel damageObject = new DamageModel(null, objs);
        float startTimeNano = System.nanoTime();*/
        /*for (int i = 0; i < PANELCOUNT; i++) {
            CollisionData data = objs[i].collideRayWithSelf(new Ray(new Vect(-4, 0, 0), new Vect(1, 0, 0)));
            if (data != null) {
                pennedDist += data.getImpactThickness(sD);
                Ray ray = data.getOutRay();
            }
        }*/
        //damageObject.calculateProjectileStrike(new Ray(new Vect(2, 0, 0), new Vect(-1, 0, 0)),20000, true);
        //System.out.println("Finished: "+(System.nanoTime()-startTimeNano)+"ns - "+(System.nanoTime()-startTimeNano)/1e9+"s");
    }
}
