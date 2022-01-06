package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.EntityInterface;
import net.cjsah.mod.carpet.patches.EntityPlayerMPFake;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInterface
{
    @Shadow
    public float yaw;
    
    @Shadow
    public float prevYaw;

    @Shadow public @Nullable abstract Entity getPrimaryPassenger();

    @Shadow public Level world;

    public float getMainYaw(float partialTicks)
    {
        return partialTicks == 1.0F ? this.yaw : Mth.lerp(partialTicks, this.prevYaw, this.yaw);
    }

    @Inject(method = "isLogicalSideForUpdatingMovement", at = @At("HEAD"), cancellable = true)
    private void isFakePlayer(CallbackInfoReturnable<Boolean> cir)
    {
        if (getPrimaryPassenger() instanceof EntityPlayerMPFake) cir.setReturnValue(!world.isClientSide);
    }
}
