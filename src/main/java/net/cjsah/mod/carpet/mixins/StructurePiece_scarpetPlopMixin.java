package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructurePiece.class)
public class StructurePiece_scarpetPlopMixin {
    @Redirect(method = "addBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;markBlockForPostProcessing(Lnet/minecraft/util/math/BlockPos;)V"
    ))
    private void markOrNot(ChunkAccess chunk, BlockPos pos) {
        if (!CarpetSettings.skipGenerationChecks.get()) chunk.markPosForPostprocessing(pos);
    }
}
