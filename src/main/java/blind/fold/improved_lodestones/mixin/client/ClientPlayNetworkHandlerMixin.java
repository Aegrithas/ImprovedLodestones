package blind.fold.improved_lodestones.mixin.client;

import blind.fold.improved_lodestones.*;
import blind.fold.improved_lodestones.client.ClientPlayNetworkHandlerExt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static blind.fold.improved_lodestones.PlayerEntityExt.openEditLodestoneScreen;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener, ClientPlayPacketListenerExt, ClientPlayNetworkHandlerExt {
  
  @Final
  @Shadow
  private MinecraftClient client;
  
  @Shadow
  private ClientWorld world;
  
  @Shadow private ClientWorld.Properties worldProperties;
  @Unique
  private LodestoneManager lodestoneManager = null;
  
  @Unique
  @Override
  public LodestoneManager getLodestoneManager() {
    return this.lodestoneManager;
  }
  
  @Unique
  @Override
  public void onSynchronizeLodestones(SynchronizeLodestonesS2CPacket packet) {
    NetworkThreadUtils.forceMainThread(packet, this, this.client);
    this.lodestoneManager = LodestoneManager.createClientLodestoneManager(packet.states());
  }
  
  @Unique
  @Override
  public void onLodestoneEditorOpen(LodestoneEditorOpenS2CPacket packet) {
    NetworkThreadUtils.forceMainThread(packet, this, this.client);
    var pos = packet.pos();
    if (this.world.getBlockEntity(pos) instanceof LodestoneBlockEntity blockEntity) {
      openEditLodestoneScreen(this.client.player, blockEntity);
    } else {
      var blockEntity = new LodestoneBlockEntity(pos, this.world.getBlockState(pos));
      blockEntity.setWorld(this.world);
      openEditLodestoneScreen(this.client.player, blockEntity);
    }
  }
  
  @Unique
  @Override
  public void onLodestoneUpdate(LodestoneUpdateS2CPacket packet) {
    NetworkThreadUtils.forceMainThread(packet, this, this.client);
    this.lodestoneManager.setState(packet.dimension(), packet.pos(), packet.state());
  }
  
  @Unique
  @Override
  public void onGameRuleUpdate(GameRuleUpdateS2CPacket packet) {
    NetworkThreadUtils.forceMainThread(packet, this, this.client);
    assert this.client.world != null;
    this.client.world.getGameRules().get(ImprovedLodestonesGameRules.NORTH_COMPASS_WORKS_EVERYWHERE).set(packet.northCompassWorksEverywhere(), null);
  }
  
  @Inject(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;<init>(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/entry/RegistryEntry;IILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)V"))
  private void copyGameRulesToNewDimension(PlayerRespawnS2CPacket packet, CallbackInfo info) {
    if (this.world != null) {
      this.worldProperties.getGameRules().setAllValues(this.world.getGameRules(), null);
    }
  }
  
}
