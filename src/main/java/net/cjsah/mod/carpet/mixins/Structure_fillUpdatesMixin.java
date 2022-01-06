package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureTemplate.class)
public class Structure_fillUpdatesMixin
{
    @Redirect( method = "place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/ServerWorldAccess;updateNeighbors(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"
    ))
    private void skipUpdateNeighbours(ServerLevelAccessor serverWorldAccess, BlockPos pos, Block block)
    {
        if (!CarpetSettings.impendingFillSkipUpdates.get())
            serverWorldAccess.blockUpdated(pos, block);
    }

    @Redirect(method = "place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/structure/StructurePlacementData;shouldUpdateNeighbors()Z"
    ))
    private boolean skipPostprocess(StructurePlaceSettings structurePlacementData)
    {
        return structurePlacementData.getKnownShape() || CarpetSettings.impendingFillSkipUpdates.get();
    }
}
