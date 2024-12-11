package me.kotos.minethunder.versions;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class Version_1_21_3 implements VersionInterface{
    @Override
    public void teleport(Entity ent, Location loc, float yaw, float pitch) {
        ((CraftEntity) ent).getHandle().b(loc.getX(), loc.getY(), loc.getZ(), yaw, pitch);
    }
}
