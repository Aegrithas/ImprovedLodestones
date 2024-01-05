package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.LodestoneManager;
import blind.fold.improved_lodestones.MinecraftServerExt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerExt {
  
  @Unique
  private LodestoneManager lodestoneManager;
  
  @Inject(method = "createWorlds", at = @At("TAIL"))
  private void setLodestoneManager(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo info) {
    this.lodestoneManager = LodestoneManager.createServerLodestoneManager((MinecraftServer) (Object) this);
  }
  
  @Unique
  @Override
  public LodestoneManager getLodestoneManager() {
    return this.lodestoneManager;
  }
  
}
