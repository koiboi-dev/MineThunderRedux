package me.kotos.minethunder.damagemodel.datatypes;

public class Ray implements Cloneable{
    private Vect origin;
    private Vect dir;
    public Ray(Vect origin, Vect dir) {
        this.origin = origin;
        this.dir = dir.getNormalized();
    }

    public Vect getOrigin() {
        return origin.clone();
    }

    public void setOrigin(Vect origin) {
        this.origin = origin;
    }

    public Vect getDir() {
        return dir.clone();
    }

    public void setDir(Vect dir) {
        this.dir = dir.getNormalized();
    }
    public void setDirSkipNormalised(Vect dir){
        this.dir = dir;
    }
    public Ray rotateRayAroundPoint(Vect point, double roll, double pitch, double yaw) {
        Vect difference = point.clone().sub(getOrigin());
        setOrigin(difference.rotateAroundZ(roll).rotateAroundX(pitch).rotateAroundY(yaw));
        setDir(getDir().rotateAroundZ(roll).rotateAroundX(pitch).rotateAroundY(yaw));
        return this;
    }

    @Override
    public Ray clone() {
        try {
            Ray clone = (Ray) super.clone();
            clone.setOrigin(getOrigin().clone());
            clone.setDir(getDir().clone());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
