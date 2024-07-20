package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.ItemExt;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
  
  @Shadow
  public abstract Item getItem();
  
  @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/item/tooltip/TooltipType;)V", shift = At.Shift.AFTER))
  private void appendPlayerTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info, @Local List<Text> tooltip) {
    if (player != null) {
      ItemExt.appendPlayerTooltip(this.getItem(), (ItemStack) (Object) this, context, tooltip, type, player);
    }
  }
  
}
