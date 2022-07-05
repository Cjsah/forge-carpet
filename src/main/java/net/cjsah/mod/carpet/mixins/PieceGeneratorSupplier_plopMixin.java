package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(PieceGeneratorSupplier.class)
public interface PieceGeneratorSupplier_plopMixin
{
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "lambda$simple$0", at = @At(
            value = "INVOKE",
            target = "java/util/function/Predicate.test(Ljava/lang/Object;)Z"
    ))
    private static boolean checkMate(Predicate<Object> predicate, Object o)
    {
        return CarpetSettings.skipGenerationChecks.get() || predicate.test(o);
    }
}
