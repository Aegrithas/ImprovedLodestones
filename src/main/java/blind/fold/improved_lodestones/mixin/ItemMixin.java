package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.ItemExt;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemExt {
  
  @Unique
  @Override
  public void appendPlayerTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, @NotNull PlayerEntity player) {}
  
}
