package blind.fold.improved_lodestones;

import net.minecraft.network.listener.ServerPlayPacketListener;

public interface ServerPlayPacketListenerExt {
  
  void onUpdateLodestone(UpdateLodestoneC2SPacket packet);
  
  static ServerPlayPacketListenerExt asExt(ServerPlayPacketListener self) {
    return (ServerPlayPacketListenerExt) self;
  }
  
  static void onUpdateLodestone(ServerPlayPacketListener self, UpdateLodestoneC2SPacket packet) {
    asExt(self).onUpdateLodestone(packet);
  }
  
}
