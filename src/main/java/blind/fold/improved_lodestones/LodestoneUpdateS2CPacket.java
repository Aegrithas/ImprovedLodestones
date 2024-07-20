package blind.fold.improved_lodestones;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onLodestoneUpdate;

public record LodestoneUpdateS2CPacket(RegistryKey<World> dimension, BlockPos pos, LodestoneState state) implements Packet<ClientPlayPacketListener> {
  
  public static final PacketCodec<ByteBuf, LodestoneUpdateS2CPacket> CODEC = PacketCodec.tuple(RegistryKey.createPacketCodec(RegistryKeys.WORLD), LodestoneUpdateS2CPacket::dimension, BlockPos.PACKET_CODEC, LodestoneUpdateS2CPacket::pos, LodestoneState.PACKET_CODEC, LodestoneUpdateS2CPacket::state, LodestoneUpdateS2CPacket::new);
  
  @Override
  public PacketType<LodestoneUpdateS2CPacket> getPacketId() {
    return ImprovedLodestones.PlayPackets.UPDATE_LODESTONE_PLAY;
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onLodestoneUpdate(listener, this);
  }
  
}