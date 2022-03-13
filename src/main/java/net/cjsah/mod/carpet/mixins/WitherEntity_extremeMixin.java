package net.cjsah.mod.carpet.mixins;


import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@Mixin(WitherBoss.class)
public class WitherEntity_extremeMixin {
    @Redirect(method = "shootSkullAt(ILnet/minecraft/entity/LivingEntity;)V", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextFloat()F")
    )
    private float nextFloatAmplfied(Random random) {
        if (CarpetSettings.extremeBehaviours) return random.nextFloat()/100;
        return random.nextFloat();
    }

}
