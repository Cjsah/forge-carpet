package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.script.CarpetEventServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(value = Explosion.class, priority = 990)
public abstract class Explosion_scarpetEventMixin
{
    @Shadow @Final private Level world;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private float power;
    @Shadow @Final private boolean createFire;
    @Shadow @Final private List<BlockPos> affectedBlocks;
    @Shadow @Final private Explosion.BlockInteraction destructionType;
    @Shadow @Final private @Nullable Entity entity;

    @Shadow /*@Nullable*/ public abstract /*@Nullable*/ LivingEntity getCausingEntity();

    @Shadow public static float getExposure(Vec3 source, Entity entity) {return 0.0f;}

    private List<Entity> affectedEntities;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)V",
            at = @At(value = "RETURN"))
    private void onExplosionCreated(Level world, Entity entity, DamageSource damageSource, ExplosionDamageCalculator explosionBehavior, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType, CallbackInfo ci)
    {
        if (CarpetEventServer.Event.EXPLOSION_OUTCOME.isNeeded() && !world.isClientSide())
        {
            affectedEntities = new ArrayList<>();
        }
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at=@At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/explosion/Explosion;getExposure(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)F")
    )
    private float onExplosion(Vec3 source, Entity entity)
    {
        if (affectedEntities != null)
        {
            affectedEntities.add(entity);
        }
        return getExposure(source, entity);
    }

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void onExplosion(boolean spawnParticles, CallbackInfo ci)
    {
        if (CarpetEventServer.Event.EXPLOSION_OUTCOME.isNeeded() && !world.isClientSide())
        {
            CarpetEventServer.Event.EXPLOSION_OUTCOME.onExplosion((ServerLevel) world, entity, this::getCausingEntity, x, y, z, power, createFire, affectedBlocks, affectedEntities, destructionType);
        }
    }
}
