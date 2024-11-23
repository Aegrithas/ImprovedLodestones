package blind.fold.improved_lodestones;

import net.minecraft.network.listener.ClientPlayPacketListener;

public interface ClientPlayPacketListenerExt {
  
  void onSynchronizeLodestones(SynchronizeLodestonesS2CPacket packet);
  
  void onLodestoneEditorOpen(LodestoneEditorOpenS2CPacket packet);
  
  void onLodestoneUpdate(LodestoneUpdateS2CPacket packet);
  
  void onGameRuleUpdate(GameRuleUpdateS2CPacket packet);
  
  static ClientPlayPacketListenerExt asExt(ClientPlayPacketListener self) {
    return (ClientPlayPacketListenerExt) self;
  }
  
  static void onSynchronizedLodestones(ClientPlayPacketListener self, SynchronizeLodestonesS2CPacket packet) {
    asExt(self).onSynchronizeLodestones(packet);
  }
  
  static void onLodestoneEditorOpen(ClientPlayPacketListener self, LodestoneEditorOpenS2CPacket packet) {
    asExt(self).onLodestoneEditorOpen(packet);
  }
  
  static void onLodestoneUpdate(ClientPlayPacketListener self, LodestoneUpdateS2CPacket packet) {
    asExt(self).onLodestoneUpdate(packet);
  }
  
  static void onGameRuleUpdate(ClientPlayPacketListener self, GameRuleUpdateS2CPacket packet) {
    asExt(self).onGameRuleUpdate(packet);
  }
  
}
