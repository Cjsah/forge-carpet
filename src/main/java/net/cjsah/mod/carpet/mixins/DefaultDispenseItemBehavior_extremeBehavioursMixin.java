package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.utils.RandomTools;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(DefaultDispenseItemBehavior.class)
public class DefaultDispenseItemBehavior_extremeBehavioursMixin
{
    @Redirect(method = "spawnItem", expect = 3, at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextGaussian()D"
    ))
    private static double nextGauBian(Random random)
    {
        return RandomTools.nextGauBian(random);
    }

}
