package me.kotos.minethunder.damagemodel.datatypes;

import jline.internal.Nullable;
import me.kotos.minethunder.damagemodel.objects.CollisionType;

public class TriPlane {
    private final Vect S1;
    private final Vect S2;
    private final Vect S3;
    private final CollisionType type;

    public TriPlane(Vect s1, Vect s2, Vect s3, CollisionType type) {
        S1 = s1;
        S2 = s2;
        S3 = s3;
        this.type = type;
    }

    public Vect getNormal(){
        return S2.clone().sub(S1).cross(S3.clone().sub(S1)).getNormalized();
    }
@Nullable
    public Vect intersectRayWithSelf(Ray ray) {
        //Vector3 S1, Vector3 S2, Vector3 S3) {
        // 1.
        Vect dS21 = S2.clone().sub(S1);
        //System.out.println("diff s1 to s2 " + dS21);
        Vect dS31 = S3.clone().sub(S1);
        //System.out.println("diff s1 to s3 " + dS31);
        //System.out.println(S2);
        //System.out.println(S3);
        Vect n = dS21.cross(dS31);
        //System.out.println("Normal: "+ n);

        Vect dR = ray.getDir().getNormalized();
        //System.out.println(dR);

        double ndotdR = n.dot(dR);
        //System.out.println(ndotdR);

        if (Math.abs(ndotdR) < 1e-6f) { // Choose your tolerance
            return null;
        }

        double t = -n.dot(ray.getOrigin().sub(S1)) / ndotdR;
        if (t <= 0){
            return null;
        }
        //System.out.println(t);
        Vect M = ray.getOrigin().add(dR.scale(t));
        //System.out.println(M);

        // 3.
        Vect dMS1 = M.clone().sub(S1);
        double u = dMS1.dot(dS21);
        double v = dMS1.dot(dS31);
        //System.out.println(u + " : " + v);
        //System.out.println(dS21.dot(dS21) + " : " + dS31.dot(dS31));

        // 4.
        if (type == CollisionType.SQUARE) {
            if (u >= 0.0f && u <= dS21.dot(dS21)
                    && v >= 0.0f && v <= dS31.dot(dS31)) return M;
        } else if (type == CollisionType.TRI) {
            Vect mid = dS21.clone().add(dS31).scale(0.5f);
            double p = dMS1.dot(mid);
            //System.out.println(p+" : "+mid.dot(mid));
            if (u >= 0.0f && u <= dS21.dot(dS21)
                    && v >= 0.0f && v <= dS31.dot(dS31)
                    && p >= 0.0f && p <= mid.dot(mid)) return M;
        }
        return null;
        //https://stackoverflow.com/questions/21114796/3d-ray-quad-intersection-test-in-java
        //https://courses.cs.washington.edu/courses/cse457/09sp/lectures/triangle_intersection.pdf
    }
    @Nullable
    public Vect intersectRayWithPlane(Ray ray) {
        //Vector3 S1, Vector3 S2, Vector3 S3) {
        // 1.
        Vect dS21 = S2.clone().sub(S1);
        //System.out.println("diff s1 to s2 " + dS21);
        Vect dS31 = S3.clone().sub(S1);
        //System.out.println("diff s1 to s3 " + dS31);
        //System.out.println(S2);
        //System.out.println(S3);
        Vect n = dS21.cross(dS31);
        //System.out.println("Normal: "+ n);

        Vect dR = ray.getDir().getNormalized();
        //System.out.println(dR);

        double ndotdR = n.dot(dR);
        //System.out.println(ndotdR);


        double t = -n.dot(ray.getOrigin().sub(S1)) / ndotdR;
        //System.out.println(t);
        return ray.getOrigin().add(dR.scale(t));
        //https://stackoverflow.com/questions/21114796/3d-ray-quad-intersection-test-in-java
        //https://courses.cs.washington.edu/courses/cse457/09sp/lectures/triangle_intersection.pdf
    }

    public Vect getS1() {
        return S1;
    }

    public Vect getS2() {
        return S2;
    }

    public Vect getS3() {
        return S3;
    }

    public CollisionType getType() {
        return type;
    }
}