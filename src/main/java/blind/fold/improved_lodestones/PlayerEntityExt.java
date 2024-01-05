package blind.fold.improved_lodestones;

import net.minecraft.entity.player.PlayerEntity;

public interface PlayerEntityExt {
  
  void openEditLodestoneScreen(LodestoneBlockEntity lodestone);
  
  static PlayerEntityExt asExt(PlayerEntity self) {
    return (PlayerEntityExt) self;
  }
  
  static void openEditLodestoneScreen(PlayerEntity self, LodestoneBlockEntity lodestone) {
    asExt(self).openEditLodestoneScreen(lodestone);
  }
  
}
