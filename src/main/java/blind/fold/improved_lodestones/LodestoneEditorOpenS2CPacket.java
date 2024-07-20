package blind.fold.improved_lodestones;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.math.BlockPos;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onLodestoneEditorOpen;

public record LodestoneEditorOpenS2CPacket(BlockPos pos) implements Packet<ClientPlayPacketListener> {
  
  public static final PacketCodec<ByteBuf, LodestoneEditorOpenS2CPacket> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, LodestoneEditorOpenS2CPacket::pos, LodestoneEditorOpenS2CPacket::new);
  
  @Override
  public PacketType<LodestoneEditorOpenS2CPacket> getPacketId() {
    return ImprovedLodestones.PlayPackets.OPEN_LODESTONE_EDITOR;
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onLodestoneEditorOpen(listener, this);
  }
  
}
