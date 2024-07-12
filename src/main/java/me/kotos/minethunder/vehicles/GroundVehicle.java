package me.kotos.minethunder.vehicles;

import me.kotos.minethunder.updaters.Updatable;
import me.kotos.minethunder.utils.DisplayUtils;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.vehicles.settings.GroundVehicleSettings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class GroundVehicle extends Vehicle implements Updatable {
    private float desiredRoll;
    private float desiredPitch;
    private float speed;
    private float ySpeed = 0;
    private static final float GRAVITY = 0.08f;
    //private final PacketListener packetListener;
    private double offsetY;
    private static final float DESIRED_MULT = 4;
    private final int HEIGHT;
    private final int WIDTH;
    private final int LENGTH;
    private Location prevLoc;
    private float prevYaw;
    public GroundVehicle(Location loc, String id) {
        super(loc, id);
        offsetY = (float) loc.getY();
        prevLoc = loc;

        this.register();
        /*packetListener = new PacketAdapter(MineThunder.getInstance(), ListenerPriority.HIGH, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (isPlayerInVehicle(event.getPlayer())){
                    Seat seat = getPlayerSeatInVehicle(event.getPlayer());
                    if (Arrays.asList(seat.getTypes()).contains(SeatTypes.DRIVER)){
                        //event.getPacket().getFloat().getValues().get(1) : Forward / Back
                        //event.getPacket().getFloat().getValues().get(0) : Left / Right
                        //event.getPacket().getBooleans().read(0) : Jump
                        //event.getPacket().getBooleans().read(1) : Crouch
                        //speed += event.getPacket().getFloat().getValues().get(1)/10;
                        //yaw += event.getPacket().getFloat().getValues().get(0)*5;
                        throttle = event.getPacket().getFloat().getValues().get(1);
                        turn = event.getPacket().getFloat().getValues().get(0);
                        //accelerateVehicle(event.getPacket().getFloat().getValues().get(1), event.getPacket().getFloat().getValues().get(0) == 0);
                        //turnVehicle(event.getPacket().getFloat().getValues().get(0));
                    }
                }
            }
        };
        MineThunder.getProtocolManager().addPacketListener(packetListener);*/
        WIDTH = getSettings().getHitboxSize()[0];
        HEIGHT = getSettings().getHitboxSize()[1];
        LENGTH = getSettings().getHitboxSize()[2];
    }
    @Override
    public void tick() {
        accelerateVehicle();
        applyDrag();
        turnVehicle();

        ySpeed -= GRAVITY;

        setLoc(getLoc().add(new Vector(Math.sin(Math.toRadians(getYaw())), 0, Math.cos(Math.toRadians(getYaw()))).multiply(speed)));

        RayTraceResult out = getLoc().getWorld().rayTraceBlocks(getLoc().add(0, HEIGHT, 0), new Vector(0, -1, 0), Math.abs(ySpeed)+HEIGHT, FluidCollisionMode.SOURCE_ONLY, true);
        if (out != null){
            //setLoc(out.getHitPosition().toLocation(getLoc().getWorld()));
            if (out.getHitPosition().toLocation(getLoc().getWorld()).getBlock().isPassable()) {
                ySpeed = 0;
            } else {
                ySpeed = Math.max(GRAVITY*2, ySpeed+GRAVITY);
            }
        }
        setLoc(getLoc().add(0, ySpeed, 0));

        updateCollisionPoints();

        //setRoll(getRoll()+VectorUtils.clamp(desiredRoll-getRoll(), -(Math.abs(speed)*DESIRED_MULT)-(), Math.abs(speed)*DESIRED_MULT+0.2f));
        setPitch(getPitch()+VectorUtils.clamp(desiredPitch-getPitch(), -(Math.abs(speed)*DESIRED_MULT)-(Math.abs(desiredPitch-getPitch()) > 10 ? 2 : 0), Math.abs(speed)*DESIRED_MULT+(Math.abs(desiredPitch-getPitch()) > 10 ? 2 : 0)));
        if (Math.abs(desiredPitch-getPitch()) > 10){
            speed *= 0.75;
        }

        updateSeats();
        DisplayUtils.moveDisplayEntity(getModel(), getLoc().clone().add(0, getSettings().getDisplayScale().getY()/2, 0), getRoll(), getPitch(), getYaw());
        /*currentTick++;
        if (currentTick >= 5) {
            updateCollisionPoints();
            currentTick = 0;
        }*/
        if (getSeated().size() != 0) {
            TextComponent comp = new TextComponent(String.valueOf(speed));
            getSeated().get(0).spigot().sendMessage(ChatMessageType.ACTION_BAR, comp);
        }
        prevLoc = getLoc();
        prevYaw = getYaw();
    }

    private void accelerateVehicle(){
        speed += getYInput()*getSettings().getAcceleration();//(((float)getSettings().engineForce()*throttle)/getSettings().mass())/20;
    }
    private void applyDrag(){
        double drag = getSettings().getBaseDrag();
        drag -= ((Math.abs(getPitch()*(getPitch()>0 ? 0.2 : 1)))/25)*getSettings().getTiltDrag();
        drag -= Math.abs(getXInput())*getSettings().getTurningDrag();
        speed *= drag;
        if (Math.abs(speed) <= getSettings().getAcceleration()/2) {
            speed = 0;
        }
    }
    private void turnVehicle(){
        if (Math.abs(speed) <= getSettings().getAcceleration()*4 && Math.abs(getXInput()) >= 0.01) {
            if (speed > 0) {
                speed = getSettings().getAcceleration() * 4;
            } else {
                speed = -getSettings().getAcceleration() * 4;
            }
        }
        setYaw(getYaw()+getXInput()*getSettings().getTurnSpeed()*((speed+getSettings().getAcceleration())/getSettings().getMaxSpeed()));
    }
    public void updateCollisionPoints(){
        Vector backLeft = new Vector(WIDTH, HEIGHT, -LENGTH).add(getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(getPitch())).rotateAroundY(Math.toRadians(getYaw()));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, getLoc().add(backLeft), 0, new Particle.DustOptions(Color.GREEN, 2));
        RayTraceResult backLeftOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(backLeft),new Vector(0, -1, 0), HEIGHT*4+ySpeed, FluidCollisionMode.SOURCE_ONLY, true);

        Vector frontLeft = new Vector(WIDTH, HEIGHT, LENGTH).add(getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(getPitch())).rotateAroundY(Math.toRadians(getYaw()));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, getLoc().add(frontLeft), 0, new Particle.DustOptions(Color.RED, 2));
        RayTraceResult frontLeftOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(frontLeft),new Vector(0, -1, 0), HEIGHT*4+ySpeed, FluidCollisionMode.SOURCE_ONLY, true);

        Vector backRight = new Vector(-WIDTH, HEIGHT, -LENGTH).add(getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(getPitch())).rotateAroundY(Math.toRadians(getYaw()));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, getLoc().add(backRight), 0, new Particle.DustOptions(Color.YELLOW, 2));
        RayTraceResult backRightOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(backRight),new Vector(0, -1, 0), HEIGHT*4+ySpeed, FluidCollisionMode.SOURCE_ONLY, true);

        Vector frontRight = new Vector(-WIDTH, HEIGHT, LENGTH).add(getSettings().getHitboxOffset()).rotateAroundX(Math.toRadians(getPitch())).rotateAroundY(Math.toRadians(getYaw()));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, getLoc().add(frontRight), 0, new Particle.DustOptions(Color.BLUE, 2));
        RayTraceResult frontRightOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(frontRight),new Vector(0, -1, 0), HEIGHT*4+ySpeed, FluidCollisionMode.SOURCE_ONLY, true);

        if (backLeftOut == null || frontLeftOut == null || backRightOut == null || frontRightOut == null) {
            return;
        }
        RayTraceResult moveOut;
        RayTraceResult sideOut;
        if (speed >= 0.5) {
            RayTraceResult leftSpeedLine = getLoc().getWorld().rayTraceBlocks(getLoc().add(frontLeft), frontLeft.clone().subtract(backLeft), speed);
            if (leftSpeedLine != null && !leftSpeedLine.getHitBlock().getRelative(BlockFace.UP).isPassable()){
                speed = (float) leftSpeedLine.getHitPosition().distance(getLoc().add(frontLeft).toVector());
            }
            RayTraceResult rightSpeedLine = getLoc().getWorld().rayTraceBlocks(getLoc().add(frontRight), frontRight.clone().subtract(backRight), speed);
            if (rightSpeedLine != null && !rightSpeedLine.getHitBlock().getRelative(BlockFace.UP).isPassable()){
                speed = (float) rightSpeedLine.getHitPosition().distance(getLoc().add(frontRight).toVector());
            }
        }
        if (speed >= 0){
            moveOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(frontRight), frontLeft.clone().subtract(frontRight), WIDTH*2);
        } else {
            moveOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(backRight), backLeft.clone().subtract(backRight), WIDTH*2);
        }
        if (getXInput() > 0) {
            sideOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(backLeft), frontLeft.clone().subtract(backLeft), LENGTH*2-0.01);
        } else {
            sideOut = getLoc().getWorld().rayTraceBlocks(getLoc().add(backRight), frontRight.clone().subtract(backRight), LENGTH*2-0.01);
        }
        if ((moveOut != null && !moveOut.getHitBlock().getRelative(BlockFace.UP).isPassable())) {
            /*Vector pushDir = moveOut.getHitPosition().subtract(getLoc().toVector()).normalize().multiply(speed);
            pushDir.setY(Math.min(pushDir.getY(), 0));
            setLoc(getLoc().subtract(pushDir));*/
            /*if (speed >= backLeftOut.getHitBlock().getBlockData().getMaterial().getBlastResistance()) {
                backLeftOut.getHitBlock().breakNaturally();
                return;
            }*/
            setLoc(prevLoc);
            speed = Math.min(speed, getSettings().getMaxSpeed()/4);
            return;
        } else if (sideOut != null && !sideOut.getHitBlock().getRelative(BlockFace.UP).isPassable()) {
            /*Vector pushDir = sideOut.getHitPosition().subtract(getLoc().toVector()).normalize().multiply(speed);
            pushDir.setY(Math.min(pushDir.getY(), 0));
            setLoc(getLoc().subtract(pushDir));*/
            setLoc(prevLoc);
            setYaw(prevYaw);
            speed = Math.min(speed, getSettings().getMaxSpeed()/2);
            return;
        }
        /*(if (!backLeftOut.getHitBlock().getRelative(BlockFace.UP).isPassable() || !frontLeftOut.getHitBlock().getRelative(BlockFace.UP).isPassable() || !backRightOut.getHitBlock().getRelative(BlockFace.UP).isPassable() || !frontRightOut.getHitBlock().getRelative(BlockFace.UP).isPassable()) {
            setLoc(prevLoc);
            speed *= 0.75;
            return;
        }*/

        Vector backLeftVec = backLeftOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(backLeftOut.getHitBlock()));
        Vector frontLeftVec = frontLeftOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(frontLeftOut.getHitBlock()));
        Vector backRightVec = backRightOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(backRightOut.getHitBlock()));
        Vector frontRightVec = frontRightOut.getHitPosition().setY(VectorUtils.getFalseYFromNeighbours(frontRightOut.getHitBlock()));

        getLoc().getWorld().spawnParticle(Particle.REDSTONE, backRightOut.getHitPosition().toLocation(getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, frontLeftOut.getHitPosition().toLocation(getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, backRightOut.getHitPosition().toLocation(getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1));
        getLoc().getWorld().spawnParticle(Particle.REDSTONE, frontRightOut.getHitPosition().toLocation(getLoc().getWorld()), 0, new Particle.DustOptions(Color.WHITE, 1));


        desiredPitch = 0;
        desiredPitch += getPitchAngleBetweenCoords(backLeftVec, frontLeftVec);
        desiredPitch += getPitchAngleBetweenCoords(backRightVec, frontRightVec);
        desiredPitch /= 2;
        desiredPitch = Math.min(Math.max(desiredPitch, -25), 25);

        setLoc(getLoc().getX(), (backLeftVec.getY()+backRightVec.getY()+frontLeftVec.getY()+frontRightVec.getY())/4, getLoc().getZ());

        //desiredRoll = 0;
        //desiredRoll += getRollAngleBetweenCoords(backLeftVec, backRightVec);
        //desiredRoll += getRollAngleBetweenCoords(frontLeftVec, frontRightVec);
        //desiredRoll /= 2;
        //desiredRoll = Math.min(Math.max(desiredRoll, -25), 25);

        //double desiredY = ((backLeftOut.getHitPosition().getY()+backRightOut.getHitPosition().getY()+frontLeftOut.getHitPosition().getY()+frontRightOut.getHitPosition().getY())/4);
        //offsetY += (float) Math.min(Math.max( -speed/4), speed/4);
        //setLoc(backLeftOut.getHitPosition().clone().add(backRightOut.getHitPosition()).add(frontLeftOut.getHitPosition()).add(frontRightOut.getHitPosition()).divide(new Vector(4, 4, 4)).toLocation(getLoc().getWorld()));
        //MineThunder.getInstance().getLogger().info("LOC: "+getLoc().getX()+" : "+getLoc().getY()+" : "+getLoc().getZ());
    }

    private float getPitchAngleBetweenCoords(Vector pos1, Vector pos2){
        return (float) Math.toDegrees(Math.atan((pos1.getY()-pos2.getY())/LENGTH));
    }
    private float getRollAngleBetweenCoords(Vector pos1, Vector pos2){
        return (float) Math.toDegrees(Math.atan((pos1.getY()-pos2.getY())/WIDTH));
    }
    @Override
    public GroundVehicleSettings getSettings() {
        return (GroundVehicleSettings) super.getSettings();
    }
}
