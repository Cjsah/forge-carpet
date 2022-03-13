package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.utils.RandomTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import net.minecraft.world.Containers;

@Mixin(Containers.class)
public class ItemScatterer_extremeMixin {
    @Redirect(method = "spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",  expect = 3, at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextGaussian()D"
    ))
    private static double nextGauBian(Random random) {
        return RandomTools.nextGauBian(random);
    }
}
