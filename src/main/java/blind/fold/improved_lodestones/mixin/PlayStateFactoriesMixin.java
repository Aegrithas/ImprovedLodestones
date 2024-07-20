package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.ImprovedLodestones.PlayPackets;
import blind.fold.improved_lodestones.LodestoneEditorOpenS2CPacket;
import blind.fold.improved_lodestones.LodestoneUpdateS2CPacket;
import blind.fold.improved_lodestones.SynchronizeLodestonesS2CPacket;
import blind.fold.improved_lodestones.UpdateLodestoneC2SPacket;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.state.PlayStateFactories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayStateFactories.class)
public abstract class PlayStateFactoriesMixin {
  
  @Inject(method = "method_55959", at = @At("TAIL"))
  private static void addC2SPackets(NetworkStateBuilder<ServerPlayPacketListener, RegistryByteBuf> builder, CallbackInfo info) {
    builder.add(PlayPackets.LODESTONE_UPDATE, UpdateLodestoneC2SPacket.CODEC);
  }
  
  @Inject(method = "method_55958", at = @At("TAIL"))
  private static void addS2CPackets(NetworkStateBuilder<ClientPlayPacketListener, RegistryByteBuf> builder, CallbackInfo info) {
    builder.add(PlayPackets.UPDATE_LODESTONES_JOIN, SynchronizeLodestonesS2CPacket.CODEC).add(PlayPackets.OPEN_LODESTONE_EDITOR, LodestoneEditorOpenS2CPacket.CODEC).add(PlayPackets.UPDATE_LODESTONE_PLAY, LodestoneUpdateS2CPacket.CODEC);
  }
  
}
