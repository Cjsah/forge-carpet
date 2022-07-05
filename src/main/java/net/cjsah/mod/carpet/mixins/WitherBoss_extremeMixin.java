package net.cjsah.mod.carpet.mixins;


import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(WitherBoss.class)
public class WitherBoss_extremeMixin
{
    @Redirect(method = "performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextFloat()F")
    )
    private float nextFloatAmplfied(Random random)
    {
        if (CarpetSettings.extremeBehaviours) return random.nextFloat()/100;
        return random.nextFloat();
    }

}
