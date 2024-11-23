package blind.fold.improved_lodestones;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.GameRules;

import static blind.fold.improved_lodestones.ClientPlayPacketListenerExt.onGameRuleUpdate;

public record GameRuleUpdateS2CPacket(boolean northCompassWorksEverywhere) implements Packet<ClientPlayPacketListener> {
  
  public GameRuleUpdateS2CPacket(GameRules gameRules) {
    this(gameRules.getBoolean(ImprovedLodestonesGameRules.NORTH_COMPASS_WORKS_EVERYWHERE));
  }
  
  public GameRuleUpdateS2CPacket(PacketByteBuf buf) {
    this(buf.readBoolean());
  }
  
  @Override
  public void write(PacketByteBuf buf) {
    buf.writeBoolean(northCompassWorksEverywhere);
  }
  
  @Override
  public void apply(ClientPlayPacketListener listener) {
    onGameRuleUpdate(listener, this);
  }
  
}
