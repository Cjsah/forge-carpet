package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.utils.RandomTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public class MobEntity_extremeMixin {
    @Redirect(method = "initialize", expect = 1, at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextGaussian()D"
    ))
    private double nextGauBian(Random random) {
        return RandomTools.nextGauBian(random);
    }
}
