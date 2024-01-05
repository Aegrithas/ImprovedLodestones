package blind.fold.improved_lodestones.client;

import blind.fold.improved_lodestones.ClientPlayPacketListenerExt;
import blind.fold.improved_lodestones.LodestoneManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Environment(EnvType.CLIENT)
public interface ClientPlayNetworkHandlerExt extends ClientPlayPacketListenerExt {
  
  LodestoneManager getLodestoneManager();
  
  static ClientPlayNetworkHandlerExt asExt(ClientPlayNetworkHandler self) {
    return (ClientPlayNetworkHandlerExt) self;
  }
  
  static LodestoneManager getLodestoneManager(ClientPlayNetworkHandler self) {
    return asExt(self).getLodestoneManager();
  }
  
}
