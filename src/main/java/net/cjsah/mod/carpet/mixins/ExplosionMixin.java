package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.helpers.OptimizedExplosion;
import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.logging.LoggerRegistry;
import net.cjsah.mod.carpet.logging.logHelpers.ExplosionLogHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(value = Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private List<BlockPos> affectedBlocks;

    @Shadow @Final private Level world;

    private ExplosionLogHelper eLogger;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("HEAD"),
            cancellable = true)
    private void onExplosionA(CallbackInfo ci) {
        if (CarpetSettings.optimizedTNT) {
            OptimizedExplosion.doExplosionA((Explosion) (Object) this, eLogger);
            ci.cancel();
        }
    }

    @Inject(method = "affectWorld", at = @At("HEAD"),
            cancellable = true)
    private void onExplosionB(boolean spawnParticles, CallbackInfo ci) {
        if (eLogger != null) {
            eLogger.setAffectBlocks( ! affectedBlocks.isEmpty());
            eLogger.onExplosionDone(this.world.getGameTime());
        }
        if (CarpetSettings.explosionNoBlockDamage) {
            affectedBlocks.clear();
        }
        if (CarpetSettings.optimizedTNT) {
            OptimizedExplosion.doExplosionB((Explosion) (Object) this, spawnParticles);
            ci.cancel();
        }
    }
    //optional due to Overwrite in Lithium
    //should kill most checks if no block damage is requested
    @Redirect(method = "collectBlocksAndDamageEntities", require = 0, at = @At(value = "INVOKE",
            target ="Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState noBlockCalcsWithNoBLockDamage(Level world, BlockPos pos) {
        if (CarpetSettings.explosionNoBlockDamage) return Blocks.BEDROCK.defaultBlockState();
        return world.getBlockState(pos);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)V",
            at = @At(value = "RETURN"))
    private void onExplosionCreated(Level world, Entity entity, DamageSource damageSource, ExplosionDamageCalculator explosionBehavior, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType, CallbackInfo ci) {
        if (LoggerRegistry.__explosions && ! world.isClientSide) {
            eLogger = new ExplosionLogHelper(entity, x, y, z, power, createFire, destructionType);
        }
    }

    @Redirect(method = "collectBlocksAndDamageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private void setVelocityAndUpdateLogging(Entity entity, Vec3 velocity) {
        if (eLogger != null) {
            eLogger.onEntityImpacted(entity, velocity.subtract(entity.getDeltaMovement()));
        }
        entity.setDeltaMovement(velocity);
    }
}
