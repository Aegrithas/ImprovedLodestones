package blind.fold.improved_lodestones;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static blind.fold.improved_lodestones.MinecraftServerExt.getLodestoneManager;
import static blind.fold.improved_lodestones.PlayerEntityExt.openEditLodestoneScreen;

public class LodestoneBlock extends BlockWithEntity {
  
  public static final MapCodec<LodestoneBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(createSettingsCodec()).apply(instance, LodestoneBlock::new));
  
  public LodestoneBlock(Settings settings) {
    super(settings);
  }
  
  @Override
  protected MapCodec<LodestoneBlock> getCodec() {
    return CODEC;
  }
  
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
  
  @Override
  public LodestoneBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new LodestoneBlockEntity(pos, state);
  }
  
  @Override
  protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (!(world.getBlockEntity(pos) instanceof LodestoneBlockEntity)) {
      return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    if (world.isClient) {
      return ItemActionResult.CONSUME;
    }
    if (player.getStackInHand(hand).isOf(Items.COMPASS)) {
      return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }
  
  @Override
  protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
    if (!(world.getBlockEntity(pos) instanceof LodestoneBlockEntity blockEntity)) {
      return ActionResult.PASS;
    }
    if (world.isClient) {
      Util.throwOrPause(new IllegalStateException("Expected to only call this on server"));
    }
    if (!this.isOtherPlayerEditing(player, blockEntity) && player.canModifyBlocks()) {
      this.openEditScreen(player, blockEntity);
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;
  }
  
  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    super.onPlaced(world, pos, state, placer, itemStack);
    if (world instanceof ServerWorld serverWorld) {
      var server = serverWorld.getServer();
      getLodestoneManager(server).broadcastSetState(server, world.getRegistryKey(), pos, new LodestoneState.Anonymous());
    }
  }
  
  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    super.onStateReplaced(state, world, pos, newState, moved);
    if (!state.isOf(newState.getBlock()) && world instanceof ServerWorld serverWorld) {
      var server = serverWorld.getServer();
      getLodestoneManager(server).broadcastSetState(server, world.getRegistryKey(), pos, new LodestoneState.Destroyed());
    }
  }
  
  public void openEditScreen(PlayerEntity player, LodestoneBlockEntity blockEntity) {
    blockEntity.setEditor(player.getUuid());
    openEditLodestoneScreen(player, blockEntity);
  }
  
  private boolean isOtherPlayerEditing(PlayerEntity player, LodestoneBlockEntity lodestone) {
    var uuid = lodestone.getEditor();
    return uuid != null && !uuid.equals(player.getUuid());
  }
  
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return validateTicker(type, ImprovedLodestones.LODESTONE_BLOCK_ENTITY, LodestoneBlockEntity::tick);
  }
  
}
