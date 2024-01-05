package blind.fold.improved_lodestones.client;

import blind.fold.improved_lodestones.LodestoneManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ImprovedLodestonesClient implements ClientModInitializer {
  
  @Override
  public void onInitializeClient() {
  
  }
  
  public static LodestoneManager getLodestoneManager() {
    return ClientPlayNetworkHandlerExt.getLodestoneManager(MinecraftClient.getInstance().getNetworkHandler());
  }
  
}
