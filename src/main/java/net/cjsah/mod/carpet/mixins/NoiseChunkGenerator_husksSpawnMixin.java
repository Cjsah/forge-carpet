package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

@Mixin(NoiseBasedChunkGenerator.class)
// todo rename mixin after 1.15 is gone
public abstract class NoiseChunkGenerator_husksSpawnMixin extends ChunkGenerator {
    public NoiseChunkGenerator_husksSpawnMixin(BiomeSource biomeSource, StructureSettings structuresConfig) {
        super(biomeSource, structuresConfig);
    }

    @Inject(method = "getEntitySpawnList", at = @At("HEAD"), cancellable = true)
    private void isInsidePyramid(Biome biome, StructureFeatureManager accessor, MobCategory group, BlockPos pos, CallbackInfoReturnable<WeightedRandomList<MobSpawnSettings.SpawnerData>> cir) {
        if (CarpetSettings.huskSpawningInTemples && group == MobCategory.MONSTER) {
            if (accessor.getStructureAt(pos, true, StructureFeature.DESERT_PYRAMID).hasChildren()) {
                cir.setReturnValue(StructureFeature.DESERT_PYRAMID.getMonsterSpawns());
            }
        }
        if (CarpetSettings.shulkerSpawningInEndCities && MobCategory.MONSTER == group) {
            if (accessor.getStructureAt(pos, true, StructureFeature.END_CITY).hasChildren()) {
                cir.setReturnValue(StructureFeature.END_CITY.getMonsterSpawns());
            }
        }
    }
}
