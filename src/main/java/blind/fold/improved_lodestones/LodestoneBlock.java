package blind.fold.improved_lodestones;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static blind.fold.improved_lodestones.MinecraftServerExt.getLodestoneManager;
import static blind.fold.improved_lodestones.PlayerEntityExt.openEditLodestoneScreen;

public class LodestoneBlock extends BlockWithEntity {
  
  public LodestoneBlock(Settings settings) {
    super(settings);
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
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (world.getBlockEntity(pos) instanceof LodestoneBlockEntity blockEntity && !player.getStackInHand(hand).isOf(Items.COMPASS)) {
      if (!world.isClient) {
        if (!this.isOtherPlayerEditing(player, blockEntity) && player.canModifyBlocks()) {
          this.openEditScreen(player, blockEntity);
          return ActionResult.SUCCESS;
        } else {
          return ActionResult.PASS;
        }
      } else {
        return ActionResult.CONSUME;
      }
    } else {
      return ActionResult.PASS;
    }
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
  @SuppressWarnings("deprecation")
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
    return checkType(type, ImprovedLodestones.LODESTONE_BLOCK_ENTITY, LodestoneBlockEntity::tick);
  }
  
}
