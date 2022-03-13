package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkAccess.class)
public abstract class ChunkMixin {
    // failed to mixin into interface. need to mixin in two places that uses it
    @Inject(method = "markBlockForPostProcessing(Lnet/minecraft/util/math/BlockPos;)V",
        at =@At("HEAD"), cancellable = true)
    private void squashWarnings(BlockPos blockPos_1, CallbackInfo ci) {
        if (CarpetSettings.skipGenerationChecks.get()) ci.cancel();
    }
}
