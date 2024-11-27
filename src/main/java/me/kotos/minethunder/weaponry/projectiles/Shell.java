package me.kotos.minethunder.weaponry.projectiles;

import me.kotos.minethunder.damagemodel.datatypes.Ray;
import me.kotos.minethunder.damagemodel.datatypes.ShellData;
import me.kotos.minethunder.damagemodel.datatypes.Vect;
import me.kotos.minethunder.updaters.Updatable;
import me.kotos.minethunder.updaters.Updater;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.vehicles.Vehicle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;

public class Shell implements Updatable {
    private final Location loc;
    private final Vector velocity;
    private final ShellData data;
    private final BlockDisplay display;

    public Shell(Location loc, ShellData data) {
        this.data = data;
        this.velocity = loc.getDirection().multiply(data.launchSpeed()/20);
        this.loc = loc.clone();
        display = (BlockDisplay) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        display.setTransformation(new Transformation(new Vector3f(-0.1f, 0, -0.1f), new AxisAngle4f(), new Vector3f(0.2f, 0.2f, 2), new AxisAngle4f()));
        loc.setDirection(velocity.clone().normalize());
        display.setTeleportDuration(1);
        display.setBlock(Material.SEA_LANTERN.createBlockData());
        display.setDisplayWidth(0.1f);
        display.setDisplayHeight(0.1f);
        this.register();
    }

    @Override
    public void tick() {
        velocity.add(new Vector(0, -0.1, 0));
        velocity.multiply(0.995f);
        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.GRAY, 3));//new Particle.DustOptions(Color.GRAY, 2f));
        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc.clone().add(velocity.clone().multiply(0.5)), 1, new Particle.DustOptions(Color.GRAY, 3));//new Particle.DustOptions(Color.GRAY, 2f));
        loc.add(velocity);
        loc.setDirection(velocity.clone().normalize());
        display.teleport(loc);
        if (!loc.getWorld().getBlockAt(loc).isPassable()){
            onCollide(loc);
        } else if (velocity.lengthSquared() > 1) {
            RayTraceResult out = loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), velocity.length());
            if (out != null) {
                onCollide(out.getHitPosition().toLocation(loc.getWorld()));
            }
        }
        Ray ray = new Ray(new Vect(loc.toVector()), new Vect(velocity));
        for (Vehicle v : Updater.getAllVehicles()) {
            v.getDamageModel().calculateProjectileStrike(
                    ray,
                    data.penetration(),
                    false
            );
        }
    }

    public void onCollide(Location loc){
        if (data.tntPower() != 0){
            loc.getWorld().createExplosion(loc, data.tntPower());
        }
        this.destroy();
    }

    @Override
    public void destroy() {
        display.remove();
        Updatable.super.destroy();
    }
}
