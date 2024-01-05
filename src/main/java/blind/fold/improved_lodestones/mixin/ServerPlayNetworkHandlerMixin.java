package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.UpdateLodestoneC2SPacket;
import blind.fold.improved_lodestones.LodestoneBlockEntity;
import blind.fold.improved_lodestones.ServerPlayPacketListenerExt;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener, ServerPlayPacketListenerExt {
  
  @Shadow
  public ServerPlayerEntity player;
  
  @Unique
  @Override
  @SuppressWarnings("deprecation")
  public void onUpdateLodestone(UpdateLodestoneC2SPacket packet) {
    NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
    this.player.updateLastActionTime();
    var world = this.player.getServerWorld();
    var pos = packet.pos();
    if (world.isChunkLoaded(pos)) {
      var blockEntity = world.getBlockEntity(pos);
      if (!(blockEntity instanceof LodestoneBlockEntity lodestoneBlockEntity)) return;
      lodestoneBlockEntity.tryChangeState(player, packet.state());
    }
  }
  
}
