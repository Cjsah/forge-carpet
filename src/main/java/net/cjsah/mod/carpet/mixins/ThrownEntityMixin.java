package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.logging.LoggerRegistry;
import net.cjsah.mod.carpet.logging.logHelpers.TrajectoryLogHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrowableProjectile.class)
public abstract class ThrownEntityMixin extends Entity {
    private TrajectoryLogHelper logHelper;
    public ThrownEntityMixin(EntityType<?> entityType_1, Level world_1) { super(entityType_1, world_1); }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void addLogger(EntityType<? extends Projectile> entityType_1, Level world_1, CallbackInfo ci) {
        if (LoggerRegistry.__projectiles && !world_1.isClientSide)
            logHelper = new TrajectoryLogHelper("projectiles");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickCheck(CallbackInfo ci) {
        if (LoggerRegistry.__projectiles && logHelper != null)
            logHelper.onTick(getX(), getY(), getZ(), getDeltaMovement());
    }

    @Override
    public void remove(Entity.RemovalReason arg) {
        super.remove(arg);
        if (LoggerRegistry.__projectiles && logHelper != null)
            logHelper.onFinish();
    }
}
