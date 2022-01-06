package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{

    public LivingEntityMixin(EntityType<?> type, Level world)
    {
        super(type, world);
    }

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasCustomName()Z"))
    private boolean shouldLogDeaths(LivingEntity livingEntity)
    {
        return livingEntity.hasCustomName() && CarpetSettings.cleanLogs && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
    }
}
