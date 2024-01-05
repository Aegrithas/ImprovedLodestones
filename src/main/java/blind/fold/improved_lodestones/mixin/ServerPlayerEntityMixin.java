package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.LodestoneBlockEntity;
import blind.fold.improved_lodestones.LodestoneEditorOpenS2CPacket;
import blind.fold.improved_lodestones.PlayerEntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity implements PlayerEntityExt {
  
  public ServerPlayerEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }
  
  @Shadow
  public ServerPlayNetworkHandler networkHandler;
  
  @Unique
  @Override
  public void openEditLodestoneScreen(LodestoneBlockEntity blockEntity) {
    this.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.getWorld(), blockEntity.getPos()));
    this.networkHandler.sendPacket(new LodestoneEditorOpenS2CPacket(blockEntity.getPos()));
  }
  
}
