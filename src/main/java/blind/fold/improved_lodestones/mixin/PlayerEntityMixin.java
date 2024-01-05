package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.LodestoneBlockEntity;
import blind.fold.improved_lodestones.PlayerEntityExt;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityExt {
  
  @Unique
  @Override
  public void openEditLodestoneScreen(LodestoneBlockEntity entity) {}
  
}
