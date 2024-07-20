package blind.fold.improved_lodestones;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.math.BlockPos;

import static blind.fold.improved_lodestones.ServerPlayPacketListenerExt.onUpdateLodestone;

public record UpdateLodestoneC2SPacket(BlockPos pos, LodestoneState.Existing state) implements Packet<ServerPlayPacketListener> {
  
  public static final PacketCodec<ByteBuf, UpdateLodestoneC2SPacket> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, UpdateLodestoneC2SPacket::pos, LodestoneState.Existing.PACKET_CODEC, UpdateLodestoneC2SPacket::state, UpdateLodestoneC2SPacket::new);
  
  @Override
  public PacketType<UpdateLodestoneC2SPacket> getPacketId() {
    return ImprovedLodestones.PlayPackets.LODESTONE_UPDATE;
  }
  
  @Override
  public void apply(ServerPlayPacketListener listener) {
    onUpdateLodestone(listener, this);
  }
  
}