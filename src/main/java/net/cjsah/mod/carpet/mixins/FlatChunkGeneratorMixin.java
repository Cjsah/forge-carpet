package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

@Mixin(FlatLevelSource.class)
public abstract class FlatChunkGeneratorMixin extends ChunkGenerator {

    public FlatChunkGeneratorMixin(BiomeSource biomeSource, StructureSettings structuresConfig) {
        super(biomeSource, structuresConfig);
    }

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Biome biome, StructureFeatureManager accessor, MobCategory group, BlockPos pos) {
        if (CarpetSettings.flatWorldStructureSpawning) {
            if (accessor.getStructureAt(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {  //swamp hut
                if (group == MobCategory.MONSTER) {
                    return StructureFeature.SWAMP_HUT.getMonsterSpawns();
                }

                if (group == MobCategory.CREATURE) {
                    return StructureFeature.SWAMP_HUT.getCreatureSpawns();
                }
            }

            if (group == MobCategory.MONSTER) {
                if (accessor.getStructureAt(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
                    return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
                }

                if (CarpetSettings.huskSpawningInTemples) {
                    if (accessor.getStructureAt(pos, true, StructureFeature.DESERT_PYRAMID).hasChildren()) {
                        return StructureFeature.DESERT_PYRAMID.getMonsterSpawns();
                    }
                }

                if (accessor.getStructureAt(pos, false, StructureFeature.OCEAN_MONUMENT).hasChildren()) {
                    return StructureFeature.OCEAN_MONUMENT.getMonsterSpawns();
                }

                if (accessor.getStructureAt(pos, true, StructureFeature.NETHER_BRIDGE).hasChildren()) {
                    return StructureFeature.NETHER_BRIDGE.getMonsterSpawns();
                }

                if (CarpetSettings.shulkerSpawningInEndCities) {
                    if (accessor.getStructureAt(pos, true, StructureFeature.END_CITY).hasChildren()) {
                        return StructureFeature.END_CITY.getMonsterSpawns();
                    }
                }
            }
        }
        return super.getMobsAt(biome, accessor, group, pos);
    }
}
