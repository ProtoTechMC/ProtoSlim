package io.github.prototechmc.protoslim.mixin;

import io.github.prototechmc.protoslim.ProtoSlim;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin extends GolemEntity implements Monster {
    @Shadow @Final
    protected static TrackedData<Byte> COLOR;

    @Shadow
    protected abstract boolean isClosed();

    @Shadow
    protected abstract boolean tryTeleport();

    @Unique
    private boolean didTryTeleport;

    protected ShulkerEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ShulkerEntity;tryTeleport()Z"))
    private void onTryTeleport(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        didTryTeleport = true;
    }

    @Inject(method = "damage", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ShulkerEntity;tryTeleport()Z")), at = @At(value = "RETURN", ordinal = 0))
    private void afterTryTeleport(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ProtoSlim.enabled && !didTryTeleport && source.isProjectile()) {
            Entity projectile = source.getSource();
            if (projectile != null && projectile.getType() == EntityType.SHULKER_BULLET) {
                this.spawnNewShulker();
            }
        }
        didTryTeleport = false;
    }

    @Unique
    private DyeColor getColorServer() {
        Byte color = this.dataTracker.get(COLOR);
        return color != 16 && color <= 15 ? DyeColor.byId(color) : null;
    }

    @Unique
    private void spawnNewShulker() {
        Vec3d vec3d = this.getPos();
        Box box = this.getBoundingBox();
        if (!this.isClosed() && this.tryTeleport()) {
            int i = this.world.getEntitiesByType(EntityType.SHULKER, box.expand(8.0D), Entity::isAlive).size();
            float f = (float)(i - 1) / 5.0F;
            if (this.world.random.nextFloat() >= f) {
                ShulkerEntity shulkerEntity = EntityType.SHULKER.create(this.world);
                DyeColor dyeColor = this.getColorServer();
                if (dyeColor != null) {
                    shulkerEntity.getDataTracker().set(COLOR, (byte)dyeColor.getId());
                }

                shulkerEntity.refreshPositionAfterTeleport(vec3d);
                this.world.spawnEntity(shulkerEntity);
            }
        }
    }
}
