package me.kotos.minethunder.vehicles;

import com.comphenix.protocol.events.PacketAdapter;
import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.enums.SeatType;
import me.kotos.minethunder.utils.DisplayUtils;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.versions.VersionHandler;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class Seat {
    private final Vector pos;
    private final String name;
    private final SeatType[] types;
    private final Interaction inter;
    private final ItemDisplay seat;
    private Player seated = null;
    private Vehicle parent;
    private PacketAdapter adapter;

    public Seat(Vehicle parent, Vector pos, String name, SeatType[] types, int seatModel) {
        this.parent = parent;
        this.pos = pos;
        this.name = name;
        this.types = types;
        inter = (Interaction) parent.getLoc().getWorld().spawnEntity(parent.getLoc(), EntityType.INTERACTION);
        inter.setResponsive(true);
        seat = (ItemDisplay) parent.getLoc().getWorld().spawnEntity(parent.getLoc(), EntityType.ITEM_DISPLAY);
        seat.setTeleportDuration(1);
        if (seatModel != -1) {
            seat.setItemStack(DisplayUtils.getItemFromID(seatModel));
        }
        for (SeatType type: types) {
            if (type.getPacketAdapter(this) != null){
                adapter = type.getPacketAdapter(this);
                MineThunder.getProtocolManager().addPacketListener(adapter);
                System.out.println(this.name+" setup adapter for "+type);
                break;
            }
        }
    }

    private long lastStamp = 0;
    public void tick(){
        if (inter.getLastInteraction() != null && lastStamp != inter.getLastInteraction().getTimestamp() && seated == null){
            System.out.println("Entered Seat!");
            seated = (Player) inter.getLastInteraction().getPlayer();
            lastStamp = inter.getLastInteraction().getTimestamp();
            seat.addPassenger(seated);
            parent.getSeated().add(seated);
        }
        if (seated != null && (seat.getPassengers().size() == 0 || seat.getPassengers().get(0) != seated)){
            // Player left seat!
            System.out.println("Exited Seat!");
            parent.getSeated().remove(seated);
            seated = null;
        }
    }

    public void teleport() {
        Vector nV = pos.clone();
        //nV.rotateAroundZ(Math.toRadians(roll)).rotateAroundX(Math.toRadians(pitch)).rotateAroundY(Math.toRadians(yaw));
        VectorUtils.rotateVectorByEuler(nV, parent.getRoll(), parent.getPitch(), parent.getYaw());
        VersionHandler.teleport(inter, parent.getLoc().add(nV));
        DisplayUtils.moveDisplayEntity(seat, parent.getLoc().add(nV), parent.getRoll(), parent.getPitch(), parent.getYaw());
    }
    public void remove() {
        MineThunder.getProtocolManager().removePacketListener(adapter);
        seat.eject();
        tick();
        inter.remove();
        seat.remove();
    }

    public Vector getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public SeatType[] getTypes() {
        return types;
    }

    public Interaction getInter() {
        return inter;
    }

    public Player getSeated() {
        return seated;
    }

    public Vehicle getParent() {
        return parent;
    }

    public ItemDisplay getSeat() {
        return seat;
    }
}