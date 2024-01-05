package blind.fold.improved_lodestones;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onLodestoneUpdate;

public record LodestoneUpdateS2CPacket(RegistryKey<World> dimension, BlockPos pos, LodestoneState state) implements Packet<ClientPlayPacketListener> {
  
  public LodestoneUpdateS2CPacket(PacketByteBuf buf) {
    this(buf.readRegistryKey(RegistryKeys.WORLD), buf.readBlockPos(), LodestoneState.SERIALIZER.read(buf));
  }
  
  @Override
  public void write(PacketByteBuf buf) {
    buf.writeRegistryKey(dimension);
    buf.writeBlockPos(pos);
    LodestoneState.SERIALIZER.write(buf, state);
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onLodestoneUpdate(listener, this);
  }
  
}