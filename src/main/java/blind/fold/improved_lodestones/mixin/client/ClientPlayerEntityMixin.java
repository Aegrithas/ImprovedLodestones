package blind.fold.improved_lodestones.mixin.client;

import blind.fold.improved_lodestones.LodestoneBlockEntity;
import blind.fold.improved_lodestones.PlayerEntityExt;
import blind.fold.improved_lodestones.client.LodestoneEditScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public abstract class ClientPlayerEntityMixin implements PlayerEntityExt {
  
  @Final
  @Shadow
  protected MinecraftClient client;
  
  @Unique
  @Override
  public void openEditLodestoneScreen(LodestoneBlockEntity lodestone) {
    var screen = new LodestoneEditScreen(lodestone);
    this.client.setScreen(screen);
  }
  
}
