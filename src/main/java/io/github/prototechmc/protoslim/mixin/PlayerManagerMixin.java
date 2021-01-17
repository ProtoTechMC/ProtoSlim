package io.github.prototechmc.protoslim.mixin;

import io.github.prototechmc.protoslim.IServerPlayerEntity;
import io.github.prototechmc.protoslim.ProtoSlim;
import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", shift = At.Shift.AFTER, ordinal = 0),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;BRAND:Lnet/minecraft/util/Identifier;")))
    private void sendRegisterPacket(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        //noinspection OptionalGetWithoutIsPresent
        buf.writeString(FabricLoader.getInstance().getModContainer("protoslim").get().getMetadata().getVersion().getFriendlyString());
        buf.writeBoolean(server.isDedicated());
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(ProtoSlim.REGISTER_PACKET_ID, buf));
    }

    @Inject(method = "sendWorldInfo", at = @At("RETURN"))
    private void onSendWorldInfo(ServerPlayerEntity player, ServerWorld world, CallbackInfo ci) {
        ((IServerPlayerEntity)player).sendWorldBorderPacket();
    }
}
