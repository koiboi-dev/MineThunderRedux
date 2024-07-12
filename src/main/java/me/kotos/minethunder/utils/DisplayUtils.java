package me.kotos.minethunder.utils;

import me.kotos.minethunder.versions.VersionHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.*;

import java.lang.Math;

public class DisplayUtils {
    public static ItemDisplay getDisplayEntity(Location loc, int modelID, boolean flipped, Vector displayScale){
        loc.setPitch(0);
        loc.setYaw(0);
        ItemDisplay disp = (ItemDisplay) loc.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
        disp.setItemStack(getItemFromID(modelID));
        disp.setDisplayWidth(flipped ? -1 : 1);
        disp.setRotation(0, 0);
        disp.setTeleportDuration(1);
        disp.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
        disp.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(0, 1, 0 ,0),
                new Vector3f((float) displayScale.getX(), (float) displayScale.getY(), (float) displayScale.getZ()),
                disp.getTransformation().getRightRotation()
        ));
        return disp;
    }
    public static ItemDisplay getDisplayEntity(Location loc, int modelID, Vector displayScale){
        return getDisplayEntity(loc, modelID, false, displayScale);
    }

    public static ItemStack getItemFromID(int id){
        ItemStack item = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(id);
        item.setItemMeta(meta);
        return item;
    }

    public static void moveDisplayEntity(ItemDisplay disp, Location loc, float roll, float pitch, float yaw){
        VersionHandler.teleport(disp, loc);
        //disp.setInterpolationDelay(-1);
        disp.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                fromEulerAngles(Math.toRadians(pitch), Math.toRadians(roll), Math.toRadians(yaw)),
                disp.getTransformation().getScale(),
                disp.getTransformation().getRightRotation()
        ));
        //disp.setRotation(yaw, pitch);
    }

    public static Quaternionf fromEulerAngles(double roll, double pitch, double yaw) {
        // Apply Euler angle transformations
        // Derivation from www.euclideanspace.com
        double c1 = Math.cos(yaw / 2.0);
        double s1 = Math.sin(yaw / 2.0);
        double c2 = Math.cos(pitch / 2.0);
        double s2 = Math.sin(pitch / 2.0);
        double c3 = Math.cos(roll / 2.0);
        double s3 = Math.sin(roll / 2.0);
        double c1c2 = c1 * c2;
        double s1s2 = s1 * s2;

        return new Quaternionf(
                c1c2 * s3 + s1s2 * c3,
                s1 * c2 * c3 + c1 * s2 * s3,
                c1 * s2 * c3 - s1 * c2 * s3,
                c1c2 * c3 - s1s2 * s3
        );
    }
}
