package blind.fold.improved_lodestones;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

import static blind.fold.improved_lodestones.ServerPlayPacketListenerExt.onUpdateLodestone;

public record UpdateLodestoneC2SPacket(BlockPos pos, LodestoneState.Existing state) implements Packet<ServerPlayPacketListener> {
  
  public UpdateLodestoneC2SPacket(PacketByteBuf buf) {
    this(buf.readBlockPos(), LodestoneState.Existing.SERIALIZER.read(buf));
  }
  
  @Override
  public void write(PacketByteBuf buf) {
    buf.writeBlockPos(pos);
    LodestoneState.Existing.SERIALIZER.write(buf, state);
  }
  
  @Override
  public void apply(ServerPlayPacketListener listener) {
    onUpdateLodestone(listener, this);
  }
  
}