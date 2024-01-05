package blind.fold.improved_lodestones;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onSynchronizedLodestones;

public class SynchronizeLodestonesS2CPacket implements Packet<ClientPlayPacketListener> {
  
  private final Map<GlobalPos, LodestoneState.Existing> states;
  
  public SynchronizeLodestonesS2CPacket(Stream<Map.Entry<GlobalPos, LodestoneState.Existing>> states) {
    this.states = states.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
  }
  
  public SynchronizeLodestonesS2CPacket(PacketByteBuf buf) {
    this.states = buf.readMap(PacketByteBuf::readGlobalPos, LodestoneState.Existing.SERIALIZER::read);
  }
  
  public Map<GlobalPos, LodestoneState.Existing> states() {
    return this.states;
  }
  
  @Override
  public void write(PacketByteBuf buf) {
    buf.writeMap(this.states, PacketByteBuf::writeGlobalPos, LodestoneState.Existing.SERIALIZER::write);
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onSynchronizedLodestones(listener, this);
  }
  
}
