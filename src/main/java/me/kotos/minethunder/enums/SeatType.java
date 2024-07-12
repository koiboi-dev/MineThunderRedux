package me.kotos.minethunder.enums;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import jline.internal.Nullable;
import me.kotos.minethunder.MineThunder;
import me.kotos.minethunder.vehicles.Seat;
import me.kotos.minethunder.vehicles.Vehicle;

public enum SeatType {
    DRIVER(((seat) -> new PacketAdapter(MineThunder.getInstance(), ListenerPriority.HIGH, PacketType.Play.Client.STEER_VEHICLE) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (seat.getSeated() == event.getPlayer()) {
                seat.getParent().setXInput(event.getPacket().getFloat().getValues().get(0));
                seat.getParent().setYInput(event.getPacket().getFloat().getValues().get(1));
            }
        }
    })),
    GUNNER(null);

    @Nullable
    public final SeatPacketInterface packet;
    SeatType(@Nullable SeatPacketInterface packet){
        this.packet = packet;
    }
    public PacketAdapter getPacketAdapter(Seat seat) {
        if (packet == null){
            return null;
        }
        return packet.getPacketAdapter(seat);
    }
}
interface SeatPacketInterface {
    PacketAdapter getPacketAdapter(Seat seat);
}