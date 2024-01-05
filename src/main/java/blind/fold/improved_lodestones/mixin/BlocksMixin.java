package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.LodestoneBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public class BlocksMixin {
  
  @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=lodestone", ordinal = 0)))
  private static Block construct(AbstractBlock.Settings settings) {
    return new LodestoneBlock(settings.resistance(6.0f));
  }
  
}
