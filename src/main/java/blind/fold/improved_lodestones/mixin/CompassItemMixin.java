package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.ImprovedLodestones;
import blind.fold.improved_lodestones.ImprovedLodestonesGameRules;
import blind.fold.improved_lodestones.LodestoneState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

import static blind.fold.improved_lodestones.CompassItemExt.*;
import static blind.fold.improved_lodestones.MinecraftServerExt.getLodestoneManager;

@Mixin(CompassItem.class)
public abstract class CompassItemMixin extends ItemMixin {
  
  @Shadow
  public static boolean hasLodestone(ItemStack stack) {
    throw new UnsupportedOperationException("Mixin failed");
  }
  
  @Shadow
  private static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
    throw new UnsupportedOperationException("Mixin failed");
  }
  
  @Final
  @Shadow
  public static String LODESTONE_POS_KEY;
  
  @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;hasTypeAt(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/util/math/BlockPos;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
  private void initUnmanagedLodestones(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo info, NbtCompound nbt, Optional<RegistryKey<World>> lodestoneDimension, BlockPos lodestonePos) {
    // I can't get a RegistryKey<World> inside PointOfInterestStorage or PointOfInterestSet, so I can't record lodestone positions without moving the corresponding LodestoneManager.WorldState inside the POIStorage.
    // Instead, I'm doing this workaround where each inventoryTick that checks that the POI still exists also puts a LodestoneState in the manager if it doesn't have one, so there are no observable consequences of the fact that the POI exists while the LodestoneState doesn't
    // Bonus scuffed points to Mojang and Mixin for making it impossible to reuse their call to POIStorage::hasTypeAt, so I have to be redundant about it (at least it's O(1), I think)
    var serverWorld = (ServerWorld) world;
    if (serverWorld.getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, lodestonePos)) {
      var server = world.getServer();
      var lodestoneManager = getLodestoneManager(server);
      if (!lodestoneManager.getState(serverWorld.getRegistryKey(), lodestonePos).exists()) {
        lodestoneManager.broadcastSetState(server, serverWorld.getRegistryKey(), lodestonePos, new LodestoneState.Anonymous());
      }
    }
  }
  
  @Unique
  @Override
  protected void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo info) {
    super.appendTooltip(stack, world, tooltip, context, info);
    if (world == null) return;
    if (hasLodestone(stack)) {
      Text nameText;
      var nbt = stack.getNbt();
      var dimension = getLodestoneDimension(nbt).orElseThrow();
      var pos = NbtHelper.toBlockPos(nbt.getCompound(LODESTONE_POS_KEY));
      var state = ImprovedLodestones.getLodestoneManager(world).getState(dimension, pos);
      nameText = state.getText();
      Text dimensionText = null;
      dimension: {
        if (state instanceof LodestoneState.Destroyed) {
          dimensionText = OBFUSCATED_TEXT;
          break dimension;
        }
        var dimensionOpt = getLodestoneDimension(nbt);
        if (dimensionOpt.isPresent()) {
          var dimensionKey = dimensionOpt.get();
          if (dimensionKey.equals(world.getRegistryKey())) break dimension;
          var dimensionId = dimensionKey.getValue();
          dimensionText = Text.translatableWithFallback("dimension." + dimensionId.getNamespace() + '.' + dimensionId.getPath() + ".name", dimensionId.toString()).setStyle(LODESTONE_NAME_STYLE);
        }
      }
      if (nameText != null) {
        MutableText tooltipText;
        if (dimensionText != null) tooltipText = Text.translatable("item.minecraft.compass.target_tooltip.another_dimension", nameText, dimensionText);
        else tooltipText = Text.translatable("item.minecraft.compass.target_tooltip", nameText);
        tooltip.add(tooltipText.setStyle(LODESTONE_TOOLTIP_STYLE));
      }
    } else {
      var northText = world.getDimension().natural() || world.getGameRules().getBoolean(ImprovedLodestonesGameRules.NORTH_COMPASS_WORKS_EVERYWHERE) ? NORTH_TEXT : OBFUSCATED_TEXT;
      tooltip.add(northText);
    }
  }
  
}
