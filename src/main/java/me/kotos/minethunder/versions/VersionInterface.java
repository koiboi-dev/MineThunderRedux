package me.kotos.minethunder.versions;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface VersionInterface {
    void teleport(Entity ent, Location loc, float pitch, float yaw);
    default void teleport(Entity ent, Location loc){
        teleport(ent, loc, 0, 0);
    }
}
