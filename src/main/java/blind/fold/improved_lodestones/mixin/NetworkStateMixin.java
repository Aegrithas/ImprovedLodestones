package blind.fold.improved_lodestones.mixin;

import blind.fold.improved_lodestones.*;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(NetworkState.class)
public abstract class NetworkStateMixin {
  
  @ModifyArg(method = "<clinit>", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;setup(Lnet/minecraft/network/NetworkSide;Lnet/minecraft/network/NetworkState$PacketHandler;)Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/network/NetworkState;HANDSHAKING:Lnet/minecraft/network/NetworkState;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER), to = @At(value = "FIELD", target = "Lnet/minecraft/network/NetworkState;PLAY:Lnet/minecraft/network/NetworkState;", opcode = Opcodes.PUTSTATIC)))
  private static NetworkState.PacketHandler<ClientPlayPacketListener> setupClientbound(NetworkSide side, NetworkState.PacketHandler<ClientPlayPacketListener> packetHandler) {
    return packetHandler.register(SynchronizeLodestonesS2CPacket.class, SynchronizeLodestonesS2CPacket::new).register(LodestoneEditorOpenS2CPacket.class, LodestoneEditorOpenS2CPacket::new).register(LodestoneUpdateS2CPacket.class, LodestoneUpdateS2CPacket::new).register(GameRuleUpdateS2CPacket.class, GameRuleUpdateS2CPacket::new);
  }
  
  @ModifyArg(method = "<clinit>", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;setup(Lnet/minecraft/network/NetworkSide;Lnet/minecraft/network/NetworkState$PacketHandler;)Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;", ordinal = 1), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/network/NetworkState;HANDSHAKING:Lnet/minecraft/network/NetworkState;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER), to = @At(value = "FIELD", target = "Lnet/minecraft/network/NetworkState;PLAY:Lnet/minecraft/network/NetworkState;", opcode = Opcodes.PUTSTATIC)))
  private static NetworkState.PacketHandler<ServerPlayPacketListener> setupServerbound(NetworkSide side, NetworkState.PacketHandler<ServerPlayPacketListener> packetHandler) {
    return packetHandler.register(UpdateLodestoneC2SPacket.class, UpdateLodestoneC2SPacket::new);
  }
  
}
