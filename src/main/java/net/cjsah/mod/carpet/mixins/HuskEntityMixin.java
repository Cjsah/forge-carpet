package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Husk.class)
public class HuskEntityMixin {
    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target="Lnet/minecraft/world/ServerWorldAccess;isSkyVisible(Lnet/minecraft/util/math/BlockPos;)Z"))
    private static boolean isSkylightOrTempleVisible(ServerLevelAccessor serverWorldAccess, BlockPos pos) {
        return serverWorldAccess.canSeeSky(pos) ||
                (CarpetSettings.huskSpawningInTemples && (((ServerLevel)serverWorldAccess).structureFeatureManager().getStructureAt(pos, false, StructureFeature.DESERT_PYRAMID).hasChildren()));
    }
}
