package net.cjsah.mod.carpet.fakes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public interface StructureFeatureInterface<C> {
    boolean plopAnywhere(ServerLevel world, BlockPos pos, ChunkGenerator generator, boolean wireOnly,Biome biome, FeatureConfiguration config);
    boolean shouldStartPublicAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chpos, Biome biome, ChunkPos chunkPos, C featureConfig, LevelHeightAccessor heightLimitView);
}
