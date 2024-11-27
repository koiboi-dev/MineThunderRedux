package me.kotos.minethunder.updaters;

import me.kotos.minethunder.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Updater {
    private static final List<Updatable> updateables = new ArrayList<>();
    private static final List<Updatable> delete = new ArrayList<>();
    private static final List<Updatable> add = new ArrayList<>();

    public static void tickAll(){
        updateables.addAll(add);
        add.clear();
        for (Updatable u: updateables){
            u.tick();
        }
        updateables.removeAll(delete);
        delete.clear();
    }

    public static void addUpdatable(Updatable updateable){
        add.add(updateable);
    }

    public static void removeUpdatable(Updatable updateable){
        delete.add(updateable);
    }
    public static List<Vehicle> getAllVehicles(){
        List<Vehicle> vehicles = new ArrayList<>();
        for (Updatable u : updateables){
            if (u instanceof Vehicle veh) {
                vehicles.add(veh);
            }
        }
        return vehicles;
    }
}
