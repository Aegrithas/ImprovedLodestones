package blind.fold.improved_lodestones.mixin.client;

import blind.fold.improved_lodestones.ImprovedLodestonesGameRules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelPredicateProviderRegistry.class)
@Environment(EnvType.CLIENT)
public class ModelPredicateProviderRegistryMixin {
  
  @Inject(method = "method_43220", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CompassItem;createSpawnPos(Lnet/minecraft/world/World;)Lnet/minecraft/util/math/GlobalPos;"), cancellable = true)
  private static void createNorthPos(ClientWorld world, ItemStack stack, Entity entity, CallbackInfoReturnable<GlobalPos> info) {
    info.setReturnValue(entity.getWorld().getDimension().natural() || entity.getWorld().getGameRules().getBoolean(ImprovedLodestonesGameRules.NORTH_COMPASS_WORKS_EVERYWHERE) ? GlobalPos.create(entity.getWorld().getRegistryKey(), entity.getBlockPos().north(100)) : null);
  }
  
}
