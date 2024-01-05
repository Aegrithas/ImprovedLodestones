package blind.fold.improved_lodestones;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class LodestoneManager {
  
  protected static final String LODESTONE_NAMES_KEY = "LodestoneNames";
  protected static final String LODESTONE_POS_KEY = "Pos";
  protected static final String LODESTONE_NAME_KEY = "Name";
  
  protected final Map<RegistryKey<World>, WorldState> worlds;
  
  protected LodestoneManager(Map<RegistryKey<World>, WorldState> worlds) {
    this.worlds = worlds;
  }
  
  protected LodestoneManager() {
    this(new ConcurrentHashMap<>());
  }
  
  public static LodestoneManager createServerLodestoneManager(MinecraftServer server) {
    var self = new LodestoneManager();
    for (var worldKey : server.getWorldRegistryKeys()) {
      self.worlds.put(worldKey, WorldState.getServerState(server, worldKey));
    }
    return self;
  }
  
  @Environment(EnvType.CLIENT)
  public static LodestoneManager createClientLodestoneManager(Map<GlobalPos, LodestoneState.Existing> states) {
    var self = new LodestoneManager();
    for  (var entry : states.entrySet()) {
      self.worlds.computeIfAbsent(entry.getKey().getDimension(), key -> WorldState.createEmpty()).setState(entry.getKey().getPos(), entry.getValue());
    }
    return self;
  }
  
  public boolean isStateAvailable(RegistryKey<World> dimension, LodestoneState state) {
    var world = this.worlds.get(dimension);
    return world != null && world.isStateAvailable(state);
  }
  
  public LodestoneState getState(RegistryKey<World> dimension, BlockPos pos) {
    var world = this.worlds.get(dimension);
    return world != null ? world.getState(pos) : new LodestoneState.Destroyed();
  }
  
  public LodestoneState getState(GlobalPos pos) {
    return getState(pos.getDimension(), pos.getPos());
  }
  
  public boolean setState(RegistryKey<World> dimension, BlockPos pos, LodestoneState state) {
    if (state.exists()) {
      var world = this.worlds.computeIfAbsent(dimension, key -> WorldState.createEmpty());
      return world.setState(pos, state);
    } else {
      var world = this.worlds.get(dimension);
      if (world != null) return world.setState(pos, state);
      return true;
    }
  }
  
  public boolean setState(GlobalPos pos, LodestoneState state) {
    return setState(pos.getDimension(), pos.getPos(), state);
  }
  
  public boolean broadcastSetState(MinecraftServer server, RegistryKey<World> dimension, BlockPos pos, LodestoneState state) {
    if (this.setState(dimension, pos, state)) {
      LodestoneUpdateS2CPacket packet = new LodestoneUpdateS2CPacket(dimension, pos, state);
      var playerManager = server.getPlayerManager();
      if (playerManager != null) {
        for (var player : playerManager.getPlayerList()) {
          if (player.networkHandler != null) player.networkHandler.sendPacket(packet);
        }
      }
      return true;
    } else {
      return false;
    }
  }
  
  public Stream<Map.Entry<GlobalPos, LodestoneState.Existing>> getStates() {
    return this.worlds.entrySet().stream().flatMap(worldEntry -> worldEntry.getValue().getStates().map(posEntry -> Map.entry(GlobalPos.create(worldEntry.getKey(), posEntry.getKey()), posEntry.getValue())));
  }
  
  protected static class WorldState extends PersistentState {
    
    protected final Set<String> usedNames;
    protected final Map<BlockPos, LodestoneState.Existing> states;
    
    protected WorldState(Map<BlockPos, LodestoneState.Existing> states, Set<String> usedNames) {
      this.states = states;
      this.usedNames = usedNames;
      for (var entry : states.entrySet()) {
        if (entry.getValue() instanceof LodestoneState.Named namedState) {
          usedNames.add(namedState.name());
        }
      }
    }
    
    protected WorldState() {
      this(new ConcurrentHashMap<>(), ConcurrentHashMap.newKeySet());
    }
    
    public static WorldState createEmpty() {
      return new WorldState();
    }
    
    public static WorldState createFromNbt(NbtCompound nbt) {
      var self = createEmpty();
      self.readNbt(nbt);
      return self;
    }
    
    public static WorldState getServerState(MinecraftServer server, RegistryKey<World> worldKey) {
      var world = server.getWorld(worldKey);
      if (world == null) return null;
      var stateManager = world.getPersistentStateManager();
      return stateManager.getOrCreate(WorldState::createFromNbt, WorldState::createEmpty, ImprovedLodestones.MOD_ID);
    }
    
    public boolean isStateAvailable(LodestoneState state) {
      return !(state instanceof LodestoneState.Named namedState && this.usedNames.contains(namedState.name()));
    }
    
    public LodestoneState getState(BlockPos pos) {
      var state = this.states.get(pos);
      return state != null ? state : new LodestoneState.Destroyed();
    }
    
    public boolean setState(BlockPos pos, LodestoneState state) {
      if (!isStateAvailable(state)) return false;
      if (state instanceof LodestoneState.Existing existingState) {
        var previous = this.states.put(pos, existingState);
        if (!existingState.equals(previous)) {
          this.markDirty(); // a state was added, or an existing state was replaced
          if (previous instanceof LodestoneState.Named namedState) this.usedNames.remove(namedState.name());
          if (state instanceof LodestoneState.Named namedState) this.usedNames.add(namedState.name());
        }
      } else {
        var previous = this.states.remove(pos);
        if (previous != null) {
          this.markDirty(); // an existing state was removed
          if (previous instanceof LodestoneState.Named namedState) this.usedNames.remove(namedState.name());
        }
      }
      return true;
    }
    
    public Stream<Map.Entry<BlockPos, LodestoneState.Existing>> getStates() {
      return this.states.entrySet().stream();
    }
    
    public void readNbt(NbtCompound nbt) {
      var statesNbt = nbt.getList(LODESTONE_NAMES_KEY, NbtElement.COMPOUND_TYPE);
      if (statesNbt == null || statesNbt.isEmpty()) return;
      statesNbt.forEach(lodestoneElement -> {
        var lodestoneNbt = (NbtCompound) lodestoneElement;
        var pos = BlockPos.CODEC.parse(NbtOps.INSTANCE, lodestoneNbt.get(LODESTONE_POS_KEY)).getOrThrow(false, message -> {});
        var state = LodestoneState.Existing.SERIALIZER.readNbt(lodestoneNbt);
        this.states.put(pos, state);
      });
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
      if (this.states.isEmpty()) return nbt;
      var statesNbt = new NbtList();
      nbt.put(LODESTONE_NAMES_KEY, statesNbt);
      this.states.forEach((pos, state) -> {
        var lodestoneNbt = new NbtCompound();
        lodestoneNbt.put(LODESTONE_POS_KEY, BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).getOrThrow(false, message -> {}));
        LodestoneState.Existing.SERIALIZER.writeNbt(state, lodestoneNbt);
        statesNbt.add(lodestoneNbt);
      });
      return nbt;
    }
    
  }
  
}
