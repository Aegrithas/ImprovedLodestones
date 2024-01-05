package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.SynchronizeLodestonesS2CPacket;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
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
  
  @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER, ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeTagsS2CPacket;<init>(Ljava/util/Map;)V")), locals = LocalCapture.CAPTURE_FAILHARD)
  private void sendOnJoinPackets(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info, GameProfile gameProfile, UserCache userCache, String string, NbtCompound nbtCompound, RegistryKey<World> registryKey, ServerWorld serverWorld, ServerWorld serverWorld2, String string2, WorldProperties worldProperties, ServerPlayNetworkHandler serverPlayNetworkHandler) {
    serverPlayNetworkHandler.sendPacket(new SynchronizeLodestonesS2CPacket(getLodestoneManager(this.server).getStates()));
  }
  
}
