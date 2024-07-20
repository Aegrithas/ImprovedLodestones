package blind.fold.improved_lodestones;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.math.GlobalPos;

import java.util.HashMap;
import java.util.Map;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onSynchronizedLodestones;

public class SynchronizeLodestonesS2CPacket implements Packet<ClientPlayPacketListener> {
  
  private static final PacketCodec<ByteBuf, Map<GlobalPos, LodestoneState.Existing>> STATES_CODEC = PacketCodecs.map(HashMap::new, GlobalPos.PACKET_CODEC, LodestoneState.Existing.PACKET_CODEC);
  public static final PacketCodec<ByteBuf, SynchronizeLodestonesS2CPacket> CODEC = PacketCodec.tuple(STATES_CODEC, packet -> packet.states, SynchronizeLodestonesS2CPacket::new);
  
  private final Map<GlobalPos, LodestoneState.Existing> states;
  
  public SynchronizeLodestonesS2CPacket(Map<GlobalPos, LodestoneState.Existing> states) {
    this.states = Map.copyOf(states);
  }
  
  @Override
  public PacketType<SynchronizeLodestonesS2CPacket> getPacketId() {
    return ImprovedLodestones.PlayPackets.UPDATE_LODESTONES_JOIN;
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onSynchronizedLodestones(listener, this);
  }
  
  public Map<GlobalPos, LodestoneState.Existing> getStates() {
    return this.states;
  }
  
}
