package io.github.prototechmc.protoslim.mixin;

import io.github.prototechmc.protoslim.ProtoSlim;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Unique
    private static final Logger PROTOSLIM_LOGGER = LogManager.getLogger("protoslim");

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ProtoSlim.enabled = false;
    }

    @Inject(method = "onCustomPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (ProtoSlim.REGISTER_PACKET_ID.equals(packet.getChannel())) {
            PacketByteBuf data = packet.getData();
            String version = data.readString();
            boolean dedicatedServer = data.readBoolean();
            PROTOSLIM_LOGGER.info("Server has protoslim version {} installed. Dedicated server = {}", version, dedicatedServer);
            if (dedicatedServer) {
                ProtoSlim.enabled = true;
            }
            ci.cancel();
        }
    }
}
