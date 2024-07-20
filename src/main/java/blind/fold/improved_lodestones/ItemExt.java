package blind.fold.improved_lodestones;

import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemExt {
  
  void appendPlayerTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, @NotNull PlayerEntity player);
  
  static ItemExt asExt(Item self) {
    return (ItemExt) self;
  }
  
  static void appendPlayerTooltip(Item self, ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, @NotNull PlayerEntity player) {
    asExt(self).appendPlayerTooltip(stack, context, tooltip, type, player);
  }
  
}
