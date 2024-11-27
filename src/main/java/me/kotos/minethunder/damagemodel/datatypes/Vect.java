package me.kotos.minethunder.damagemodel.datatypes;

import com.google.common.primitives.Doubles;
import org.bukkit.util.Vector;

public class Vect implements Cloneable {
    private double x;
    private double y;
    private double z;

    public Vect(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vect(Vector v) {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
    }
    public Vect add(Vect other) {
        return new Vect(x + other.x, y + other.y, z + other.z);
    }

    public Vect sub(Vect other) {
        return new Vect(x - other.x, y - other.y, z - other.z);
    }

    public Vect scale(double f) {
        return new Vect(x * f, y * f, z * f);
    }

    public Vect cross(Vect other) {
        return new Vect(y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }
    public float distanceSquared(Vect other) {
        return (float) (((x-other.getX())*(x-other.getX()))+((y-other.getY())*(y-other.getY()))+((z-other.getZ())*(z-other.getZ())));
    }
    public float distance(Vect other) {
        return (float) Math.sqrt(((x-other.getX())*(x-other.getX()))+((y-other.getY())*(y-other.getY()))+((z-other.getZ())*(z-other.getZ())));
    }

    public double dot(Vect other) {
        return x * other.x + y * other.y + z * other.z;
    }
    public Vect reflect(Vect normal){
        return clone().sub(normal.clone().scale(2*dot(normal)));
    }
    public double getX() {
        return x;
    }

    public Vect setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public Vect setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() {
        return z;
    }

    public Vect setZ(double z) {
        this.z = z;
        return this;
    }
    public double getLength(){
        return Math.sqrt((x*x)+(y*y)+(z*z));
    }
    public Vect getNormalized(){
        double len = getLength();
        return new Vect(x/len, y/len, z/len);
    }
    public Vect rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() - angleSin * getY();
        double y = angleSin * getX() + angleCos * getY();
        return setX(x).setY(y);
    }
    public Vect rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() + angleSin * getZ();
        double z = -angleSin * getX() + angleCos * getZ();
        return setX(x).setZ(z);
    }
    public Vect rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double y = angleCos * getY() - angleSin * getZ();
        double z = angleSin * getY() + angleCos * getZ();
        return setY(y).setZ(z);
    }

    public Vect min(Vect other) {
        return new Vect(
                Math.min(getX(), other.getX()),
                Math.min(getY(), other.getY()),
                Math.min(getZ(), other.getZ())
        );
    }
    public Vect max(Vect other) {
        return new Vect(
                Math.max(getX(), other.getX()),
                Math.max(getY(), other.getY()),
                Math.max(getZ(), other.getZ())
        );
    }

    @Override
    public Vect clone() {
        try {
            Vect clone = (Vect) super.clone();
            clone.setX(clone.getX());
            clone.setY(clone.getY());
            clone.setZ(clone.getZ());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    public float angle(Vect other) {
        return (float) Math.acos(getNormalized().dot(other.getNormalized()));
        /*double dot = Doubles.constrainToRange(dot(other) / (getLength() * other.getLength()), -1.0, 1.0);

        return (float) Math.acos(dot);*/
    }
    public Vect getInverted(){
        return new Vect(-x, -y, -z);
    }

    @Override
    public String toString() {
        return "Vector("+x+","+y+","+z+")";
    }
    public static Vect getRandomVectInBox(Vect min, Vect max){
        return new Vect(
                min.getX()+(Math.random()*(max.getX()-min.getX())),
                min.getY()+(Math.random()*(max.getY()-min.getY())),
                min.getZ()+(Math.random()*(max.getZ()-min.getZ()))
        );
    }
    public Vector toVector(){
        return new Vector(
                x,
                y,
                z
        );
    }
}
