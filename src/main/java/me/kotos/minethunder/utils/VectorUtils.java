package me.kotos.minethunder.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class VectorUtils {
    public static float clamp(float value, float min, float max){
        return Math.min(Math.max(value, min), max);
    }

    public static Vector rotateVectorByEuler(Vector v, double roll, double pitch, double yaw){
        return v.rotateAroundZ(Math.toRadians(roll)).rotateAroundX(Math.toRadians(pitch)).rotateAroundY(Math.toRadians(yaw));
    }

    public static Vector getVectorFromEuler(double pitch, double yaw){
        return new Vector(0, 0, 1).rotateAroundX(Math.toRadians(pitch)).rotateAroundY(Math.toRadians(yaw));
    }
    public static float getYawDifference(float start, float end){
        float result = (end - start) % 360;
        if(result < 0) result += 360;
        result -= (result > 180 ? 360 : 0);
        return result;
    }
    public static int getBlockNeighbours(Block block){
        return (block.getRelative(BlockFace.NORTH).isPassable() ? 0 : 1)+
                (block.getRelative(BlockFace.EAST).isPassable() ? 0 : 1)+
                (block.getRelative(BlockFace.SOUTH).isPassable() ? 0 : 1)+
                (block.getRelative(BlockFace.WEST).isPassable() ? 0 : 1);
    }

    public static double getFalseYFromNeighbours(Block block) {
        return block.getY()+((block.getRelative(BlockFace.NORTH).isPassable() || block.getRelative(BlockFace.EAST).isPassable() || block.getRelative(BlockFace.SOUTH).isPassable() || (block.getRelative(BlockFace.WEST).isPassable())) ? 0.5 : 1); //(getBlockNeighbours(block)/4f);
    }
}
