package blind.fold.improved_lodestones;

import blind.fold.improved_lodestones.client.ImprovedLodestonesClient;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import net.minecraft.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class ImprovedLodestones implements ModInitializer {
  
  public static final String MOD_ID = "improved_lodestones";
  
  public static final Identifier LODESTONE_BLOCK_ENTITY_ID = identifier("lodestone");
  
  public static final BlockEntityType<LodestoneBlockEntity> LODESTONE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, LODESTONE_BLOCK_ENTITY_ID, BlockEntityType.Builder.create(LodestoneBlockEntity::new, Blocks.LODESTONE).build());
  
  @Override
  public void onInitialize() {
  
  }
  
  public static LodestoneManager getLodestoneManager(World world) {
    var server = Objects.requireNonNull(world).getServer(); // server == null implies this is a client, so ImprovedLodestonesClient is safe to reference
    return server != null ? MinecraftServerExt.getLodestoneManager(server) : ImprovedLodestonesClient.getLodestoneManager();
  }
  
  public static Identifier identifier(String path) {
    return Identifier.of(MOD_ID, path);
  }
  
  public static class PlayPackets {
    
    public static final PacketType<SynchronizeLodestonesS2CPacket> UPDATE_LODESTONES_JOIN = s2c("update_lodestones_join");
    public static final PacketType<LodestoneUpdateS2CPacket> UPDATE_LODESTONE_PLAY = s2c("update_lodestone_play");
    public static final PacketType<LodestoneEditorOpenS2CPacket> OPEN_LODESTONE_EDITOR = s2c("open_lodestone_editor");
    public static final PacketType<UpdateLodestoneC2SPacket> LODESTONE_UPDATE = c2s("lodestone_update");
    
    private static <T extends Packet<ClientPlayPacketListener>> PacketType<T> s2c(String id) {
      return new PacketType<>(NetworkSide.CLIENTBOUND, identifier(id));
    }
    
    private static <T extends Packet<ServerPlayPacketListener>> PacketType<T> c2s(String id) {
      return new PacketType<>(NetworkSide.SERVERBOUND, identifier(id));
    }
    
  }
  
}
