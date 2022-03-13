package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Mixin(StructureBlockEntity.class)
public abstract class StructureBlockBlockEntity_fillUpdatesMixin {
    @Redirect(method = "place", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/structure/Structure;place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z"
    ))
    private boolean onStructurePlacen(StructureTemplate structure, ServerLevelAccessor serverWorldAccess, BlockPos pos, BlockPos blockPos, StructurePlaceSettings placementData, Random random, int i) {
        if(!CarpetSettings.fillUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(true);
        try {
            return structure.placeInWorld(serverWorldAccess, pos, blockPos, placementData, random, i); // serverWorldAccess, pos, placementData, random);
        }
        finally {
            CarpetSettings.impendingFillSkipUpdates.set(false);
        }
    }
}