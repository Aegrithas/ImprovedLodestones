package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.SynchronizeLodestonesS2CPacket;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static blind.fold.improved_lodestones.MinecraftServerExt.getLodestoneManager;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
  
  @Final
  @Shadow
  private MinecraftServer server;
  
  @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER, ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeRecipesS2CPacket;<init>(Ljava/util/Collection;)V")), locals = LocalCapture.CAPTURE_FAILHARD)
  private void sendOnJoinPackets(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info, @Local ServerPlayNetworkHandler serverPlayNetworkHandler) {
    serverPlayNetworkHandler.sendPacket(new SynchronizeLodestonesS2CPacket(getLodestoneManager(this.server).getStates()));
  }
  
}
