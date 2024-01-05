package blind.fold.improved_lodestones;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class CompassItemExt {
  
  private CompassItemExt() {}
  
  public static final Style LODESTONE_TOOLTIP_STYLE = Style.EMPTY.withFormatting(Formatting.DARK_AQUA);
  
  public static final Style LODESTONE_NAME_STYLE = Style.EMPTY.withFormatting(Formatting.YELLOW);
  
  public static final Text ANONYMOUS_TEXT = Text.translatable("item.minecraft.compass.target_tooltip.anonymous");
  
  public static final Text OBFUSCATED_TEXT = Text.literal("nowhere").formatted(Formatting.OBFUSCATED, Formatting.GRAY);
  
  public static final Text NORTH_TEXT = Text.translatable("item.minecraft.compass.north_tooltip").formatted(Formatting.DARK_GREEN);
  
}
