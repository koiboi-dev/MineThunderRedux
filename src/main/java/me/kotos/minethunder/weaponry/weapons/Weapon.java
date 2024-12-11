package me.kotos.minethunder.weaponry.weapons;

import me.kotos.minethunder.vehicles.Turret;
import me.kotos.minethunder.vehicles.settings.WeaponSettings;
import me.kotos.minethunder.weaponry.projectiles.Shell;
import org.bukkit.util.Vector;

public class Weapon {
    private final String type;
    private final Vector pos;
    private final boolean barrelMounted;
    private final Turret parent;

    public Weapon(String type, Vector pos, boolean barrelMounted, Turret parent) {
        this.type = type;
        this.pos = pos;
        this.barrelMounted = barrelMounted;
        this.parent = parent;
    }

    private void fire(int shell) {

    }

    public WeaponSettings getSettings(){
        return WeaponSettings.getSettings(type);
    }

    public String getType() {
        return type;
    }

    public Vector getPos() {
        return pos;
    }

    public boolean isBarrelMounted() {
        return barrelMounted;
    }

    public Turret getParent() {
        return parent;
    }
}
