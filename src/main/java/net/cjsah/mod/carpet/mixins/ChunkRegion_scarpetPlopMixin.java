package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldGenRegion.class)
public class ChunkRegion_scarpetPlopMixin {
    @Inject(method = "markBlockForPostProcessing", at = @At("HEAD"), cancellable = true)
    private void markOrNot(BlockPos blockPos, CallbackInfo ci) {
        if (CarpetSettings.skipGenerationChecks.get()) ci.cancel();
    }
}
