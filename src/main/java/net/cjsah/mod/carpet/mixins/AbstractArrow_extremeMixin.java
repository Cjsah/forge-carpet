package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.utils.RandomTools;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(AbstractArrow.class)
public class AbstractArrow_extremeMixin
{
    // calculates damage
    @Redirect(method = "setEnchantmentEffectsFromEntity", expect = 1, at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextGaussian()D"
    ))
    private double nextGauBian2(Random random)
    {
        return RandomTools.nextGauBian(random);
    }

}
