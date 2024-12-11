//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.kotos.minethunder.vehicles;

import me.kotos.minethunder.updaters.Updatable;
import me.kotos.minethunder.utils.DisplayUtils;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.vehicles.settings.GroundVehicleSettings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class GroundVehicle extends Vehicle implements Updatable {
    private float desiredRoll;
    private float desiredPitch;
    private float speed;
    private float ySpeed = 0.0F;
    private static final float GRAVITY = 0.08F;
    private static final float DESIRED_MULT = 4.0F;
    private final float HEIGHT;
    private final float WIDTH;
    private final float LENGTH;
    private Location prevLoc;

    public GroundVehicle(Location loc, String id) {
        super(loc, id);
        this.prevLoc = loc;
        this.register();
        this.WIDTH = (float)this.getSettings().getHitboxSize().getX();
        this.HEIGHT = (float)this.getSettings().getHitboxSize().getY();
        this.LENGTH = (float)this.getSettings().getHitboxSize().getZ();
    }

    public void tick() {
        if (this.isEnabled()) {
            this.accelerateVehicle();
            this.applyDrag();
            this.turnVehicle();
            this.ySpeed -= 0.08F;
            this.setLoc(this.getLoc().add((new Vector(Math.sin(Math.toRadians(this.getYaw())), 0.0, Math.cos(Math.toRadians(this.getYaw())))).multiply(this.speed)));
            RayTraceResult out = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(0.0, this.HEIGHT, 0.0), new Vector(0, -1, 0), Math.abs(this.ySpeed) + this.HEIGHT, FluidCollisionMode.SOURCE_ONLY, true);
            if (out != null) {
                if (out.getHitPosition().toLocation(this.getLoc().getWorld()).getBlock().isPassable()) {
                    this.ySpeed = 0.0F;
                } else {
                    this.ySpeed = Math.max(0.16F, this.ySpeed + 0.08F);
                }
            }

            this.setLoc(this.getLoc().add(0.0, this.ySpeed, 0.0));
            this.updateCollisionPoints();
            this.setPitch(this.getPitch() + VectorUtils.clamp(this.desiredPitch - this.getPitch(), -(Math.abs(this.speed) * 4.0F) - (float)(Math.abs(this.desiredPitch - this.getPitch()) > 10.0F ? 2 : 0), Math.abs(this.speed) * 4.0F + (float)(Math.abs(this.desiredPitch - this.getPitch()) > 10.0F ? 2 : 0)));
            if (Math.abs(this.desiredPitch - this.getPitch()) > 10.0F) {
                this.speed *= 0.75F;
            }

            this.updateSeats();
            DisplayUtils.moveDisplayEntity(this.getModel(), this.getLoc().clone().add(0.0, this.getSettings().getDisplayScale().getY() / 2.0, 0.0), this.getRoll(), this.getPitch(), this.getYaw());
            if (!this.getSeated().isEmpty()) {
                TextComponent comp = new TextComponent(String.valueOf(this.speed));
                this.getSeated().get(0).spigot().sendMessage(ChatMessageType.ACTION_BAR, comp);
            }

            this.getDamageModel().renderComponents();
            this.prevLoc = this.getLoc();
        }
    }

    private void accelerateVehicle() {
        this.speed += this.getYInput() * this.getSettings().getAcceleration();
    }

    private void applyDrag() {
        double drag = this.getSettings().getBaseDrag();
        drag -= Math.abs((double)this.getPitch() * (this.getPitch() > 0.0F ? 0.2 : 1.0)) / 25.0 * (double)this.getSettings().getTiltDrag();
        drag -= Math.abs(this.getXInput()) * this.getSettings().getTurningDrag();
        this.speed = (float)((double)this.speed * drag);
        if (Math.abs(this.speed) <= this.getSettings().getAcceleration() / 2.0F) {
            this.speed = 0.0F;
        }

    }

    private void turnVehicle() {
        this.setYaw(this.getYaw() + this.getXInput() * this.getSettings().getTurnSpeed() * ((this.speed + this.getSettings().getAcceleration()) / this.getSettings().getMaxSpeed()));
    }

    public void updateCollisionPoints() {
        Vector backLeft = (new Vector(this.WIDTH, this.HEIGHT, -this.LENGTH)).add(this.getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(this.getPitch())).rotateAroundY(Math.toRadians(this.getYaw()));
        this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, this.getLoc().add(backLeft), 0, new Particle.DustOptions(Color.GREEN, 2.0F));
        RayTraceResult backLeftOut = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(backLeft), new Vector(0, -1, 0), this.HEIGHT * 4.0F + this.ySpeed, FluidCollisionMode.SOURCE_ONLY, true);
        Vector frontLeft = (new Vector(this.WIDTH, this.HEIGHT, this.LENGTH)).add(this.getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(this.getPitch())).rotateAroundY(Math.toRadians(this.getYaw()));
        this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, this.getLoc().add(frontLeft), 0, new Particle.DustOptions(Color.RED, 2.0F));
        RayTraceResult frontLeftOut = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(frontLeft), new Vector(0, -1, 0), this.HEIGHT * 4.0F + this.ySpeed, FluidCollisionMode.SOURCE_ONLY, true);
        Vector backRight = (new Vector(-this.WIDTH, this.HEIGHT, -this.LENGTH)).add(this.getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(this.getPitch())).rotateAroundY(Math.toRadians(this.getYaw()));
        this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, this.getLoc().add(backRight), 0, new Particle.DustOptions(Color.YELLOW, 2.0F));
        RayTraceResult backRightOut = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(backRight), new Vector(0, -1, 0), this.HEIGHT * 4.0F + this.ySpeed, FluidCollisionMode.SOURCE_ONLY, true);
        Vector frontRight = (new Vector(-this.WIDTH, this.HEIGHT, this.LENGTH)).add(this.getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(this.getPitch())).rotateAroundY(Math.toRadians(this.getYaw()));
        this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, this.getLoc().add(frontRight), 0, new Particle.DustOptions(Color.BLUE, 2.0F));
        RayTraceResult frontRightOut = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(frontRight), new Vector(0, -1, 0), this.HEIGHT * 4.0F + this.ySpeed, FluidCollisionMode.SOURCE_ONLY, true);
        if (backLeftOut != null && frontLeftOut != null && backRightOut != null && frontRightOut != null) {
            if ((double)this.speed >= 0.5) {
                RayTraceResult leftSpeedLine = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(frontLeft), frontLeft.clone().subtract(backLeft), this.speed);
                if (leftSpeedLine != null && !leftSpeedLine.getHitBlock().getRelative(BlockFace.UP).isPassable()) {
                    this.speed = (float)leftSpeedLine.getHitPosition().distance(this.getLoc().add(frontLeft).toVector());
                }

                RayTraceResult rightSpeedLine = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(frontRight), frontRight.clone().subtract(backRight), this.speed);
                if (rightSpeedLine != null && !rightSpeedLine.getHitBlock().getRelative(BlockFace.UP).isPassable()) {
                    this.speed = (float)rightSpeedLine.getHitPosition().distance(this.getLoc().add(frontRight).toVector());
                }
            }

            RayTraceResult moveOut;
            if (this.speed >= 0.0F) {
                moveOut = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(frontRight), frontLeft.clone().subtract(frontRight), this.WIDTH * 2.0F);
            } else {
                moveOut = this.getLoc().getWorld().rayTraceBlocks(this.getLoc().add(backRight), backLeft.clone().subtract(backRight), this.WIDTH * 2.0F);
            }

            if (moveOut != null && !moveOut.getHitBlock().getRelative(BlockFace.UP).isPassable()) {
                if (this.speed >= 1.0F) {
                    this.getLoc().getWorld().createExplosion(this.getLoc(), Math.min(this.speed, 2.0F), true);
                }

                this.setLoc(this.prevLoc);
                this.speed = Math.min(this.speed, this.getSettings().getMaxSpeed() / 4.0F);
            } else {
                Vector backLeftVec = backLeftOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(backLeftOut.getHitBlock()));
                Vector frontLeftVec = frontLeftOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(frontLeftOut.getHitBlock()));
                Vector backRightVec = backRightOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(backRightOut.getHitBlock()));
                Vector frontRightVec = frontRightOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(frontRightOut.getHitBlock()));
                this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, backRightOut.getHitPosition().toLocation(this.getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1.0F));
                this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, frontLeftOut.getHitPosition().toLocation(this.getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1.0F));
                this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, backRightOut.getHitPosition().toLocation(this.getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1.0F));
                this.getLoc().getWorld().spawnParticle(Particle.REDSTONE, frontRightOut.getHitPosition().toLocation(this.getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1.0F));
                this.desiredPitch = 0.0F;
                this.desiredPitch += this.getPitchAngleBetweenCoords(backLeftVec, frontLeftVec);
                this.desiredPitch += this.getPitchAngleBetweenCoords(backRightVec, frontRightVec);
                this.desiredPitch /= 2.0F;
                this.desiredPitch = Math.min(Math.max(this.desiredPitch, -25.0F), 25.0F);
                this.setLoc(this.getLoc().getX(), (backLeftVec.getY() + backRightVec.getY() + frontLeftVec.getY() + frontRightVec.getY()) / 4.0, this.getLoc().getZ());
            }
        }
    }

    private float getPitchAngleBetweenCoords(Vector pos1, Vector pos2) {
        return (float)Math.toDegrees(Math.atan((pos1.getY() - pos2.getY()) / (double)this.LENGTH));
    }

    private float getRollAngleBetweenCoords(Vector pos1, Vector pos2) {
        return (float)Math.toDegrees(Math.atan((pos1.getY() - pos2.getY()) / (double)this.WIDTH));
    }

    public GroundVehicleSettings getSettings() {
        return (GroundVehicleSettings)super.getSettings();
    }
}
