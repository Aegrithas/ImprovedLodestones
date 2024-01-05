package blind.fold.improved_lodestones;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onLodestoneEditorOpen;

public record LodestoneEditorOpenS2CPacket(BlockPos pos) implements Packet<ClientPlayPacketListener> {
  
  public LodestoneEditorOpenS2CPacket(PacketByteBuf buf) {
    this(buf.readBlockPos());
  }
  
  @Override
  public void write(PacketByteBuf buf) {
    buf.writeBlockPos(this.pos);
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onLodestoneEditorOpen(listener, this);
  }
  
}
