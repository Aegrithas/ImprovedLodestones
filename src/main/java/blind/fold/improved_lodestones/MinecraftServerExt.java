package blind.fold.improved_lodestones;

import net.minecraft.server.MinecraftServer;

public interface MinecraftServerExt {
  
  LodestoneManager getLodestoneManager();
  
  static MinecraftServerExt asExt(MinecraftServer self) {
    return (MinecraftServerExt) self;
  }
  
  static LodestoneManager getLodestoneManager(MinecraftServer self) {
    return asExt(self).getLodestoneManager();
  }
  
}
