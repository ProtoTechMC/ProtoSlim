package io.github.prototechmc.protoslim.mixin;

import com.mojang.authlib.GameProfile;
import io.github.prototechmc.protoslim.IServerPlayerEntity;
import io.github.prototechmc.protoslim.ProtoSlim;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IServerPlayerEntity {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Unique
    private boolean wasPositive = false;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at=@At("RETURN"))
    private void afterTick(CallbackInfo ci) {
        boolean isPositive = getZ() > 0;
        if (isPositive != wasPositive) {
            sendWorldBorderPacket();
        }
    }

    @Override
    public void sendWorldBorderPacket() {
        wasPositive = getZ() > 0;
        WorldBorder border = new WorldBorder();
        border.setSize(ProtoSlim.BORDER_LENGTH);
        border.setCenter(0, (ProtoSlim.BORDER_LENGTH - ProtoSlim.BORDER_WIDTH) / 2 * this.world.getDimension().getCoordinateScale() * -Math.signum(getZ()));
        border.setWarningBlocks(0);
        this.networkHandler.sendPacket(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_SIZE));
        this.networkHandler.sendPacket(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_CENTER));
    }
}
