package blind.fold.improved_lodestones;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;

import java.util.NoSuchElementException;
import java.util.Optional;

public sealed interface LodestoneState {
  
  sealed interface Existing extends LodestoneState {
    
    PacketCodec<ByteBuf, Existing> PACKET_CODEC = new PacketCodec<>() {
      
      @Override
      public Existing decode(ByteBuf buf) {
        var type = Type.PACKET_CODEC.decode(buf);
        return switch (type) {
          case NAMED -> Named.PACKET_CODEC.decode(buf);
          case ANONYMOUS -> Anonymous.PACKET_CODEC.decode(buf);
          case DESTROYED -> throw new IllegalArgumentException("Expected a lodestone state that exists; got '%s'".formatted(Type.DESTROYED.key()));
        };
      }
      
      @Override
      public void encode(ByteBuf buf, Existing value) {
        Type.PACKET_CODEC.encode(buf, value.type());
        switch (value) {
          case Named named -> Named.PACKET_CODEC.encode(buf, named);
          case Anonymous anonymous -> Anonymous.PACKET_CODEC.encode(buf, anonymous);
        }
      }
      
    };
    
    Serializer<Existing> SERIALIZER = new Serializer.Existing();
    
    static Existing forName(String name) {
      if (name == null) throw new IllegalArgumentException("name must not be null");
      return name.isEmpty() ? new Anonymous() : new Named(name);
    }
    
    String name();
    
    @Override
    default boolean exists() {
      return true;
    }
    
    @Override
    default Optional<Existing> asExisting() {
      return Optional.of(this);
    }
    
  }
  
  record Named(@Override String name) implements Existing {
    
    public static final int MAX_NAME_LENGTH = 48;
    public static final PacketCodec<ByteBuf, Named> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.string(MAX_NAME_LENGTH), Named::name, Named::new);
    
    public Named {
      if (name == null || name.isEmpty()) throw new IllegalArgumentException("name must not be null or empty");
      if (name.length() > MAX_NAME_LENGTH) throw new IllegalArgumentException("name must be no more than " + MAX_NAME_LENGTH + " characters long");
    }
    
    @Override
    public Type type() {
      return Type.NAMED;
    }
    
    @Override
    public boolean hasName() {
      return true;
    }
    
    @Override
    public Optional<String> getName() {
      return Optional.of(name);
    }
    
    @Override
    public Text getText() {
      return Text.literal(name).setStyle(CompassItemExt.LODESTONE_NAME_STYLE);
    }
    
  }
  
  record Anonymous() implements Existing {
    
    public static final PacketCodec<ByteBuf, Anonymous> PACKET_CODEC = PacketCodec.unit(new Anonymous());
    
    @Override
    public Type type() {
      return Type.ANONYMOUS;
    }
    
    @Override
    public String name() {
      return "";
    }
    
    @Override
    public Text getText() {
      return CompassItemExt.ANONYMOUS_TEXT;
    }
    
  }
  
  record Destroyed() implements LodestoneState {
    
    public static final PacketCodec<ByteBuf, Destroyed> PACKET_CODEC = PacketCodec.unit(new Destroyed());
    
    @Override
    public Type type() {
      return Type.DESTROYED;
    }
    
    @Override
    public boolean exists() {
      return false;
    }
    
    @Override
    public Optional<Existing> asExisting() {
      return Optional.empty();
    }
    
    @Override
    public Text getText() {
      return CompassItemExt.OBFUSCATED_TEXT;
    }
    
  }
  
  enum Type {
    
    NAMED(Serializer.NAMED_ID, Serializer.NAMED_KEY, true, true),
    ANONYMOUS(Serializer.ANONYMOUS_ID, Serializer.ANONYMOUS_KEY, true, false),
    DESTROYED(Serializer.DESTROYED_ID, Serializer.NAMED_KEY, false, false);
    
    public static final PacketCodec<ByteBuf, Type> PACKET_CODEC = new PacketCodec<>() {
      
      @Override
      public Type decode(ByteBuf buf) {
        return fromId(buf.readByte());
      }
      
      @Override
      public void encode(ByteBuf buf, Type value) {
        buf.writeByte(value.id());
      }
      
    };
    
    private final byte id;
    
    private final String key;
    
    private final boolean exists;
    
    private final boolean hasName;
    
    Type(byte id, String key, boolean exists, boolean hasName) {
      this.id = id;
      this.key = key;
      this.exists = exists;
      this.hasName = hasName;
    }
    
    public static Type fromId(byte id) {
      return switch (id) {
        case Serializer.DESTROYED_ID -> Type.DESTROYED; // this is first to make the cases go 0, 1, 2 rather than 1, 2, 0
        case Serializer.NAMED_ID -> Type.NAMED;
        case Serializer.ANONYMOUS_ID -> Type.ANONYMOUS;
        default -> throw new NoSuchElementException("Expected lodestone state type id, but got %d, which is not valid".formatted(id));
      };
    }
    
    public static Type fromKey(String key) {
      return switch (key) {
        case Serializer.NAMED_KEY -> Type.NAMED;
        case Serializer.ANONYMOUS_KEY -> Type.ANONYMOUS;
        case Serializer.DESTROYED_KEY -> Type.DESTROYED;
        default -> throw new NoSuchElementException("Expected lodestone state type, but got '%s', which is not valid".formatted(key));
      };
    }
    
    public static Optional<Type> tryFromId(byte id) {
      return Optional.ofNullable(switch (id) {
        case Serializer.DESTROYED_ID -> Type.DESTROYED; // this is first to make the cases go 0, 1, 2 rather than 1, 2, 0
        case Serializer.NAMED_ID -> Type.NAMED;
        case Serializer.ANONYMOUS_ID -> Type.ANONYMOUS;
        default -> null;
      });
    }
    
    public static Optional<Type> tryFromKey(String key) {
      return Optional.ofNullable(switch (key) {
        case Serializer.NAMED_KEY -> Type.NAMED;
        case Serializer.ANONYMOUS_KEY -> Type.ANONYMOUS;
        case Serializer.DESTROYED_KEY -> Type.DESTROYED;
        default -> null;
      });
    }
    
    public byte id() {
      return this.id;
    }
    
    public String key() {
      return this.key;
    }
    
    public boolean exists() {
      return this.exists;
    }
    
    public boolean hasName() {
      return this.hasName;
    }
    
  }
  
  Type type();
  
  default boolean hasName() {
    return false;
  }
  
  default Optional<String> getName() {
    return Optional.empty();
  }
  
  Text getText();
  
  boolean exists();
  
  Optional<LodestoneState.Existing> asExisting();
  
  PacketCodec<ByteBuf, LodestoneState> PACKET_CODEC = new PacketCodec<>() {
    
    @Override
    public LodestoneState decode(ByteBuf buf) {
      var type = Type.PACKET_CODEC.decode(buf);
      return switch (type) {
        case NAMED -> Named.PACKET_CODEC.decode(buf);
        case ANONYMOUS -> Anonymous.PACKET_CODEC.decode(buf);
        case DESTROYED -> Destroyed.PACKET_CODEC.decode(buf);
      };
    }
    
    @Override
    public void encode(ByteBuf buf, LodestoneState value) {
      Type.PACKET_CODEC.encode(buf, value.type());
      switch (value) {
        case Named named -> Named.PACKET_CODEC.encode(buf, named);
        case Anonymous anonymous -> Anonymous.PACKET_CODEC.encode(buf, anonymous);
        case Destroyed destroyed -> Destroyed.PACKET_CODEC.encode(buf, destroyed);
      }
    }
    
  };
  
  Serializer<LodestoneState> SERIALIZER = new Serializer.General();
  
  sealed abstract class Serializer<S extends LodestoneState> {
    
    private static final String TYPE_KEY = "Type";
    
    private static final String NAME_KEY = "Name";
    
    private static final String NAMED_KEY = "Named";
    
    private static final String ANONYMOUS_KEY = "Anonymous";
    
    private static final String DESTROYED_KEY = "Destroyed";
    
    private static final byte NAMED_ID = 1;
    
    private static final byte ANONYMOUS_ID = 2;
    
    private static final byte DESTROYED_ID = 0;
    
    private Serializer() {}
    
    public abstract S readNbt(NbtCompound nbt);
    
    public abstract NbtCompound writeNbt(S state, NbtCompound nbt);
    
    public NbtCompound createNbt(S state) {
      return writeNbt(state, new NbtCompound());
    }
    
    public abstract S read(PacketByteBuf buf);
    
    public abstract void write(PacketByteBuf buf, S state);
    
    private static final class General extends Serializer<LodestoneState> {
      
      @Override
      public LodestoneState readNbt(NbtCompound nbt) {
        Type type;
        if (nbt.contains(TYPE_KEY, NbtElement.STRING_TYPE)) {
          type = Type.fromKey(nbt.getString(TYPE_KEY));
        } else if (nbt.contains(TYPE_KEY, NbtElement.BYTE_TYPE)) {
          type = Type.fromId(nbt.getByte(TYPE_KEY));
        } else {
          throw new IllegalArgumentException("Expected '%s' key to be either a string or a byte".formatted(TYPE_KEY));
        }
        return switch (type) {
          case NAMED -> new Named(nbt.getString(NAME_KEY));
          case ANONYMOUS -> new Anonymous();
          case DESTROYED -> new Destroyed();
        };
      }
      
      @Override
      public NbtCompound writeNbt(LodestoneState state, NbtCompound nbt) {
        nbt.putString(TYPE_KEY, state.type().key());
        if (state instanceof Named namedState) nbt.putString(NAME_KEY, namedState.name());
        return nbt;
      }
      
      @Override
      public LodestoneState read(PacketByteBuf buf) {
        var type = Type.fromId(buf.readByte());
        return switch (type) {
          case NAMED -> new Named(buf.readString(Named.MAX_NAME_LENGTH));
          case ANONYMOUS -> new Anonymous();
          case DESTROYED -> new Destroyed();
        };
      }
      
      @Override
      public void write(PacketByteBuf buf, LodestoneState state) {
        buf.writeByte(state.type().id());
        if (state instanceof Named namedState) buf.writeString(namedState.name(), Named.MAX_NAME_LENGTH);
      }
      
    }
    
    private static final class Existing extends Serializer<LodestoneState.Existing> {
      
      @Override
      public LodestoneState.Existing readNbt(NbtCompound nbt) {
        Type type;
        if (nbt.contains(TYPE_KEY, NbtElement.STRING_TYPE)) {
          type = Type.fromKey(nbt.getString(TYPE_KEY));
        } else if (nbt.contains(TYPE_KEY, NbtElement.BYTE_TYPE)) {
          type = Type.fromId(nbt.getByte(TYPE_KEY));
        } else {
          throw new IllegalArgumentException("Expected '%s' key to be either a string or a byte".formatted(TYPE_KEY));
        }
        return switch (type) {
          case NAMED -> new Named(nbt.getString(NAME_KEY));
          case ANONYMOUS -> new Anonymous();
          case DESTROYED -> throw new IllegalArgumentException("Expected a lodestone state that exists; got '%s'".formatted(Type.DESTROYED.key()));
        };
      }
      
      @Override
      public NbtCompound writeNbt(LodestoneState.Existing state, NbtCompound nbt) {
        nbt.putString(TYPE_KEY, state.type().key());
        if (state instanceof Named namedState) nbt.putString(NAME_KEY, namedState.name());
        return nbt;
      }
      
      @Override
      public LodestoneState.Existing read(PacketByteBuf buf) {
        var type = Type.fromId(buf.readByte());
        return switch (type) {
          case NAMED -> new Named(buf.readString(Named.MAX_NAME_LENGTH));
          case ANONYMOUS -> new Anonymous();
          case DESTROYED -> throw new IllegalArgumentException("Expected a lodestone state that exists; got '%s'".formatted(Type.DESTROYED.key()));
        };
      }
      
      @Override
      public void write(PacketByteBuf buf, LodestoneState.Existing state) {
        buf.writeByte(state.type().id());
        if (state instanceof Named namedState) buf.writeString(namedState.name(), Named.MAX_NAME_LENGTH);
      }
      
    }
    
  }
  
}