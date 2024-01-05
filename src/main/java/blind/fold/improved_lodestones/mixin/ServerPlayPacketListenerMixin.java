package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.UpdateLodestoneC2SPacket;
import blind.fold.improved_lodestones.ServerPlayPacketListenerExt;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayPacketListener.class)
public interface ServerPlayPacketListenerMixin extends ServerPlayPacketListenerExt {
  
  @Unique
  @Override
  void onUpdateLodestone(UpdateLodestoneC2SPacket packet);
  
}
