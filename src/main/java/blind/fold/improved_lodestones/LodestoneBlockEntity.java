package blind.fold.improved_lodestones;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class LodestoneBlockEntity extends BlockEntity {
  
  private static final Logger LOGGER = LogUtils.getLogger();
  
  private UUID editor = null;
  
  public LodestoneBlockEntity(BlockPos pos, BlockState state) {
    super(ImprovedLodestones.LODESTONE_BLOCK_ENTITY, pos, state);
  }
  
  public Optional<LodestoneState.Existing> getState() {
    var world = this.getWorld();
    if (world == null) return Optional.empty();
    return ImprovedLodestones.getLodestoneManager(world).getState(world.getRegistryKey(), this.getPos()).asExisting();
  }
  
  public void setState(LodestoneState.Existing state) {
    if (this.isRemoved()) return;
    var world = this.getWorld();
    if (world == null) return;
    ImprovedLodestones.getLodestoneManager(world).setState(world.getRegistryKey(), this.getPos(), state);
  }
  
  public boolean isStateAvailable(LodestoneState.Existing state) {
    if (this.isRemoved()) return false;
    var world = this.getWorld();
    if (world == null) return true;
    return ImprovedLodestones.getLodestoneManager(world).isStateAvailable(world.getRegistryKey(), state);
  }
  
  public UUID getEditor() {
    return this.editor;
  }
  
  public void setEditor(UUID editor) {
    this.editor = editor;
  }
  
  public void tryChangeState(PlayerEntity player, LodestoneState.Existing state) {
    if (player.getUuid().equals(this.editor) && this.world != null) {
      var lodestoneManager = ImprovedLodestones.getLodestoneManager(this.world);
      boolean accepted;
      if (player instanceof ServerPlayerEntity serverPlayer) {
        accepted = lodestoneManager.broadcastSetState(serverPlayer.server, this.world.getRegistryKey(), this.pos, state);
      } else {
        accepted = lodestoneManager.setState(this.world.getRegistryKey(), this.pos, state);
      }
      if (!accepted) LOGGER.warn("Player {} just tried to give a lodestone a name that is already taken", player.getName().getString());
      this.setEditor(null);
    } else {
      LOGGER.warn("Player {} just tried to change non-editable lodestone", player.getName().getString());
    }
  }
  
  public boolean isPlayerTooFarToEdit(UUID uuid) {
    assert this.world != null;
    PlayerEntity playerEntity = this.world.getPlayerByUuid(uuid);
    return playerEntity == null || playerEntity.squaredDistanceTo(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) > 64.0;
  }
  
  public static void tick(World world, BlockPos pos, BlockState state, LodestoneBlockEntity lodestone) {
    var uuid = lodestone.getEditor();
    if (uuid != null) {
      lodestone.tryClearInvalidEditor(lodestone, uuid);
    }
  }
  
  private void tryClearInvalidEditor(LodestoneBlockEntity lodestone, UUID uuid) {
    if (lodestone.isPlayerTooFarToEdit(uuid)) {
      lodestone.setEditor(null);
    }
  }
  
}
