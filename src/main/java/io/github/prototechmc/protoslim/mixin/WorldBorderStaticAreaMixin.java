package io.github.prototechmc.protoslim.mixin;

import io.github.prototechmc.protoslim.ProtoSlim;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/border/WorldBorder$StaticArea")
public abstract class WorldBorderStaticAreaMixin {
    @Shadow
    private double boundEast;
    @Shadow
    private double boundNorth;
    @Shadow
    private double boundWest;
    @Shadow
    private double boundSouth;

    @Inject(method = "Lnet/minecraft/world/border/WorldBorder$StaticArea;recalculateBounds()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/border/WorldBorder$StaticArea;boundSouth:D", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void postRecalculateBounds(CallbackInfo ci) {
        if (ProtoSlim.enabled) {
            this.boundEast = ProtoSlim.BORDER_LENGTH / 2;
            this.boundNorth = -ProtoSlim.BORDER_WIDTH / 2;
            this.boundWest = -ProtoSlim.BORDER_LENGTH / 2;
            this.boundSouth = ProtoSlim.BORDER_WIDTH / 2;
        }
    }
}
