package blind.fold.improved_lodestones;

import blind.fold.improved_lodestones.client.ImprovedLodestonesClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import net.minecraft.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class ImprovedLodestones implements ModInitializer {
  
  public static final String MOD_ID = "improved_lodestones";
  
  public static final Identifier LODESTONE_BLOCK_ENTITY_ID = identifier("lodestone");
  
  public static final BlockEntityType<LodestoneBlockEntity> LODESTONE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, LODESTONE_BLOCK_ENTITY_ID, FabricBlockEntityTypeBuilder.create(LodestoneBlockEntity::new, Blocks.LODESTONE).build());
  
  @Override
  public void onInitialize() {
  
  }
  
  public static LodestoneManager getLodestoneManager(World world) {
    var server = Objects.requireNonNull(world).getServer(); // server == null implies this is a client, so ImprovedLodestonesClient is safe to reference
    return server != null ? MinecraftServerExt.getLodestoneManager(server) : ImprovedLodestonesClient.getLodestoneManager();
  }
  
  public static Identifier identifier(String path) {
    return new Identifier(MOD_ID, path);
  }
  
}
