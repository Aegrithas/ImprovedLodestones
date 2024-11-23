package blind.fold.improved_lodestones;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class ImprovedLodestonesGameRules {
  
  public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(ImprovedLodestones.identifier("gamerule_category"), Text.translatable("gamerule.category.improved_lodestones").formatted(Formatting.YELLOW, Formatting.BOLD));
  
  public static final GameRules.Key<GameRules.BooleanRule> NORTH_COMPASS_WORKS_EVERYWHERE = GameRuleRegistry.register("northCompassWorksEverywhere", CATEGORY, GameRuleFactory.createBooleanRule(false, (server, rule) -> {
    server.getPlayerManager().sendToAll(new GameRuleUpdateS2CPacket(rule.get()));
  }));
  
  public static void initialize() {}
  
}
