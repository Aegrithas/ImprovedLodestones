package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerMixin extends ClientPlayPacketListenerExt {
  
  @Unique
  @Override
  void onSynchronizeLodestones(SynchronizeLodestonesS2CPacket packet);
  
  @Unique
  @Override
  void onLodestoneEditorOpen(LodestoneEditorOpenS2CPacket packet);
  
  @Unique
  @Override
  void onLodestoneUpdate(LodestoneUpdateS2CPacket packet);
  
  @Unique
  @Override
  void onGameRuleUpdate(GameRuleUpdateS2CPacket packet);
  
}
