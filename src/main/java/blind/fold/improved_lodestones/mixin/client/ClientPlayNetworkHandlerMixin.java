package blind.fold.improved_lodestones.mixin.client;

import blind.fold.improved_lodestones.*;
import blind.fold.improved_lodestones.client.ClientPlayNetworkHandlerExt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static blind.fold.improved_lodestones.PlayerEntityExt.openEditLodestoneScreen;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements ClientPlayPacketListener, ClientPlayPacketListenerExt, ClientPlayNetworkHandlerExt {
  
  @Shadow
  private ClientWorld world;
  
  @Unique
  private LodestoneManager lodestoneManager = null;
  
  protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
    super(client, connection, connectionState);
  }
  
  @Unique
  @Override
  public LodestoneManager getLodestoneManager() {
    return this.lodestoneManager;
  }
  
  @Unique
  @Override
  public void onSynchronizeLodestones(SynchronizeLodestonesS2CPacket packet) {
    NetworkThreadUtils.forceMainThread(packet, this, this.client);
    this.lodestoneManager = LodestoneManager.createClientLodestoneManager(packet.getStates());
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
  
}
