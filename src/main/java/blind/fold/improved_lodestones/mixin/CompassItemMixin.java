package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.ImprovedLodestones;
import blind.fold.improved_lodestones.LodestoneState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static blind.fold.improved_lodestones.CompassItemExt.*;
import static blind.fold.improved_lodestones.MinecraftServerExt.getLodestoneManager;

@Mixin(CompassItem.class)
public abstract class CompassItemMixin extends ItemMixin {
  
  @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/LodestoneTrackerComponent;forWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/component/type/LodestoneTrackerComponent;", shift = At.Shift.BEFORE))
  private void initUnmanagedLodestones(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo info, @Local ServerWorld serverWorld, @Local(ordinal = 0) LodestoneTrackerComponent trackerComponent) {
    // I can't get a RegistryKey<World> inside PointOfInterestStorage or PointOfInterestSet, so I can't record lodestone positions without moving the corresponding LodestoneManager.WorldState inside the POIStorage.
    // Instead, I'm doing this workaround where each inventoryTick that checks that the POI still exists also puts a LodestoneState in the manager if it doesn't have one, so there are no observable consequences of the fact that the POI exists while the LodestoneState doesn't
    var lodestonePos = trackerComponent.target().map(GlobalPos::pos).orElse(null);
    if (lodestonePos != null && serverWorld.getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, lodestonePos)) {
      var server = world.getServer();
      var lodestoneManager = getLodestoneManager(server);
      if (!lodestoneManager.getState(serverWorld.getRegistryKey(), lodestonePos).exists()) {
        lodestoneManager.broadcastSetState(server, serverWorld.getRegistryKey(), lodestonePos, new LodestoneState.Anonymous());
      }
    }
  }
  
  @Unique
  @Override
  public void appendPlayerTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, @NotNull PlayerEntity player) {
    var world = player.getWorld();
    var trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
    if (trackerComponent != null) {
      var globalPos = trackerComponent.target().orElse(null);
      var dimension = globalPos != null ? globalPos.dimension() : null;
      var state = globalPos != null ? ImprovedLodestones.getLodestoneManager(world).getState(globalPos) : new LodestoneState.Destroyed();
      var nameText = state.getText();
      Text dimensionText = null;
      dimension: {
        if (state instanceof LodestoneState.Destroyed) {
          dimensionText = OBFUSCATED_TEXT;
          break dimension;
        }
        if (dimension != null) {
          if (dimension.equals(world.getRegistryKey())) break dimension;
          var dimensionId = dimension.getValue();
          dimensionText = Text.translatableWithFallback("dimension." + dimensionId.getNamespace() + '.' + dimensionId.getPath() + ".name", dimensionId.toString()).setStyle(LODESTONE_NAME_STYLE);
        }
      }
      MutableText tooltipText;
      if (dimensionText != null) tooltipText = Text.translatable("item.minecraft.compass.target_tooltip.another_dimension", nameText, dimensionText);
      else tooltipText = Text.translatable("item.minecraft.compass.target_tooltip", nameText);
      tooltip.add(tooltipText.setStyle(LODESTONE_TOOLTIP_STYLE));
    } else {
      tooltip.add(NORTH_TEXT);
    }
  }
  
}
