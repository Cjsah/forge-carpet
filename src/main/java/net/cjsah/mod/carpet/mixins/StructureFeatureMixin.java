package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.fakes.StructureFeatureInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(StructureFeature.class)
public abstract class StructureFeatureMixin<C extends FeatureConfiguration> implements StructureFeatureInterface<C> {
    @Shadow public abstract String getName();

    @Shadow public abstract StructureFeature.StructureStartFactory getStructureStartFactory();

    @Shadow protected abstract boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long worldSeed, ChunkRandom random, ChunkPos chunkPos, Biome biome, ChunkPos chunkPos2, C featureConfig, LevelHeightAccessor heightLimitView);

    @Override
    public boolean plopAnywhere(ServerLevel world, BlockPos pos, ChunkGenerator generator, boolean wireOnly, Biome biome, FeatureConfiguration config) {
        if (world.isClientSide())
            return false;
        CarpetSettings.skipGenerationChecks.set(true);
        try {
            Random rand = new Random(world.getRandom().nextInt());
            int j = pos.getX() >> 4;
            int k = pos.getZ() >> 4;
            long chId = ChunkPos.asLong(j, k);
            StructureStart structurestart = forceStructureStart(world, generator, rand, chId, biome, config);
            if (structurestart == StructureStart.INVALID_START) {
                return false;
            }
            world.getChunk(j, k).addReferenceForFeature((StructureFeature) (Object)this, chId);

            BoundingBox box = structurestart.setBoundingBoxFromChildren();  // getBB
            if (!wireOnly) {
                structurestart.generateStructure(world, world.structureFeatureManager(), generator, rand,box, new ChunkPos(j, k));
            }
            //structurestart.notifyPostProcessAt(new ChunkPos(j, k));
            int i = Math.max(box.getXSpan(),box.getZSpan())/16+1;

            //int i = getRadius();
            for (int k1 = j - i; k1 <= j + i; ++k1) {
                for (int l1 = k - i; l1 <= k + i; ++l1) {
                    if (k1 == j && l1 == k) continue;
                    long nbchkid = ChunkPos.asLong(k1, l1);
                    if (box.intersects(k1<<4, l1<<4, (k1<<4) + 15, (l1<<4) + 15)) {
                        world.getChunk(k1, l1).addReferenceForFeature((StructureFeature) (Object)this, chId);
                    }
                }
            }
        }
        catch (Exception booboo) {
            CarpetSettings.LOG.error("Unknown Exception while plopping structure: "+booboo, booboo);
            return false;
        }
        finally {
            CarpetSettings.skipGenerationChecks.set(false);
        }
        return true;
    }

    private StructureStart forceStructureStart(ServerLevel worldIn, ChunkGenerator generator, Random rand, long packedChunkPos, Biome biome, FeatureConfiguration config) {
        ChunkPos chunkpos = new ChunkPos(packedChunkPos);
        StructureStart structurestart;

        ChunkAccess ichunk = worldIn.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS, false);

        if (ichunk != null) {
            structurestart = ichunk.getStartForFeature((StructureFeature)(Object)this);

            if (structurestart != null && structurestart != StructureStart.INVALID_START) {
                return structurestart;
            }
        }
        Biome biome_1 = biome;
        if (biome == null)
            biome_1 = generator.getBiomeSource().getBiomeForNoiseGen((chunkpos.getMinBlockX() + 9) >> 2, 0, (chunkpos.getMinBlockZ() + 9) >> 2 );

        StructureStart structurestart1 = getStructureStartFactory().create((StructureFeature)(Object)this, chunkpos,0,worldIn.getSeed());
        if (config == null)
            config = new NoneFeatureConfiguration();
        structurestart1.init(worldIn.registryAccess(), generator, worldIn.getStructureManager() , chunkpos, biome_1, config, ichunk);
        structurestart = structurestart1.isValid() ? structurestart1 : StructureStart.INVALID_START;

        if (structurestart.isValid()) {
            worldIn.getChunk(chunkpos.x, chunkpos.z).setStartForFeature((StructureFeature)(Object)this, structurestart);
        }

        //long2objectmap.put(packedChunkPos, structurestart);
        return structurestart;
    }

    @Override
    public boolean shouldStartPublicAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, ChunkPos chpos, Biome biome, ChunkPos chunkPos, C featureConfig, LevelHeightAccessor heightLimitView) {
        return shouldStartAt(chunkGenerator, biomeSource, l, chunkRandom, chpos, biome, chunkPos, featureConfig, heightLimitView);
    }
}
