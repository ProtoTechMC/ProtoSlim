package io.github.prototechmc.protoslim.mixin;

import io.github.prototechmc.protoslim.ProtoSlim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    @Redirect(method = "Lnet/minecraft/world/border/WorldBorder$StaticArea;recalculateBounds()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/border/WorldBorder$StaticArea;boundEast:D"))
    private void setBoundEast(@Coerce Object self, double original) {
        this.boundEast = ProtoSlim.BORDER_LENGTH / 2;
    }

    @Redirect(method = "Lnet/minecraft/world/border/WorldBorder$StaticArea;recalculateBounds()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/border/WorldBorder$StaticArea;boundNorth:D"))
    private void setBoundNorth(@Coerce Object self, double original) {
        this.boundNorth = -ProtoSlim.BORDER_WIDTH / 2;
    }

    @Redirect(method = "Lnet/minecraft/world/border/WorldBorder$StaticArea;recalculateBounds()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/border/WorldBorder$StaticArea;boundWest:D"))
    private void setBoundWest(@Coerce Object self, double original) {
        this.boundWest = -ProtoSlim.BORDER_LENGTH / 2;
    }

    @Redirect(method = "Lnet/minecraft/world/border/WorldBorder$StaticArea;recalculateBounds()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/border/WorldBorder$StaticArea;boundSouth:D"))
    private void setBoundSouth(@Coerce Object self, double original) {
        this.boundSouth = ProtoSlim.BORDER_WIDTH / 2;
    }
}
