package net.cjsah.mod.carpet.helpers;

import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.fakes.StructureFeatureInterface;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FeatureGenerator {
    public static final Object boo = new Object();
    synchronized public static Boolean plop(String featureName, ServerLevel world, BlockPos pos) {
        Thing custom = featureMap.get(featureName);
        if (custom != null) {
            return custom.plop(world, pos);
        }
        ResourceLocation id = new ResourceLocation(featureName);
        ConfiguredStructureFeature<?, ?> structureFeature = world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(id);
        if (structureFeature != null) {
            return ((StructureFeatureInterface)structureFeature.feature).plopAnywhere(
                    world, pos, world.getChunkSource().getGenerator(),
                    false, world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(Biomes.PLAINS), structureFeature.config);

        }

        ConfiguredFeature<?, ?> configuredFeature = world.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).get(id);
        if (configuredFeature != null) {
            CarpetSettings.skipGenerationChecks.set(true);
            try {
                return configuredFeature.place(world, world.getChunkSource().getGenerator(), world.random, pos);
            }
            finally {
                CarpetSettings.skipGenerationChecks.set(false);
            }
        }
        StructureFeature<?> structure = Registry.STRUCTURE_FEATURE.get(id);
        if (structure != null) {
            ConfiguredStructureFeature<?,?> configuredStandard = getDefaultFeature(structure, world, pos, true);
            if (configuredStandard != null)
                return ((StructureFeatureInterface)configuredStandard.feature).plopAnywhere(
                        world, pos, world.getChunkSource().getGenerator(),
                        false, world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(Biomes.PLAINS), configuredStandard.config);

        }
        Feature<?> feature = Registry.FEATURE.get(id);
        if (feature != null) {
            ConfiguredFeature<?,?> configuredStandard = getDefaultFeature(feature, world, pos, true);
            if (configuredStandard != null) {
                CarpetSettings.skipGenerationChecks.set(true);
                try {
                    return configuredStandard.place(world, world.getChunkSource().getGenerator(), world.random, pos);
                }
                finally {
                    CarpetSettings.skipGenerationChecks.set(false);
                }
            }
        }
        return null;
    }

    public static ConfiguredStructureFeature<?, ?> resolveConfiguredStructure(String name, ServerLevel world, BlockPos pos) {
        ResourceLocation id = new ResourceLocation(name);
        ConfiguredStructureFeature<?, ?> configuredStructureFeature =  world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(id);
        if (configuredStructureFeature != null) return configuredStructureFeature;
        StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(id);
        if (structureFeature == null) return null;
        return getDefaultFeature(structureFeature, world, pos, true);
    }

    synchronized public static Boolean plopGrid(ConfiguredStructureFeature<?, ?> structureFeature, ServerLevel world, BlockPos pos) {
        return ((StructureFeatureInterface)structureFeature.feature).plopAnywhere(
                    world, pos, world.getChunkSource().getGenerator(),
                    true, net.minecraft.data.worldgen.biome.Biomes.PLAINS, structureFeature.config);
    }

    @FunctionalInterface
    private interface Thing {
        Boolean plop(ServerLevel world, BlockPos pos);
    }
    private static Thing simplePlop(ConfiguredFeature feature) {
        return (w, p) -> {
            CarpetSettings.skipGenerationChecks.set(true);
            try {
                return feature.place(w, w.getChunkSource().getGenerator(), w.random, p);
            }
            finally {
                CarpetSettings.skipGenerationChecks.set(false);
            }
        };
    }

    private static Thing simpleTree(TreeConfiguration config) {
        //config.ignoreFluidCheck();
        return simplePlop(Feature.TREE.configured(config));
    }

    private static Thing spawnCustomStructure(StructureFeature structure, FeatureConfiguration conf, ResourceKey<Biome> biome) {
        return setupCustomStructure(structure, conf, biome, false);
    }
    private static Thing setupCustomStructure(StructureFeature structure, FeatureConfiguration conf, ResourceKey<Biome> biome, boolean wireOnly) {
        return (w, p) -> ((StructureFeatureInterface)structure).plopAnywhere(w, p, w.getChunkSource().getGenerator(), wireOnly, w.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(biome), conf);
    }

    public static Boolean spawn(String name, ServerLevel world, BlockPos pos) {
        if (featureMap.containsKey(name))
            return featureMap.get(name).plop(world, pos);
        return null;
    }

    private static ConfiguredStructureFeature<?, ?> getDefaultFeature(StructureFeature<?> structure, ServerLevel world, BlockPos pos, boolean tryHard) {
        ConfiguredStructureFeature<?, ?> configuredFeature = world.getBiome(pos).getGenerationSettings().method_30978(structure.configured(null));
        if (configuredFeature.config != null || !tryHard) return configuredFeature;
        return world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).entrySet().stream().
                filter(cS -> cS.getValue().feature == structure).
                findFirst().map(Map.Entry::getValue).orElse(null);
    }

    private static ConfiguredFeature<?, ?> getDefaultFeature(Feature<?> feature, ServerLevel world, BlockPos pos, boolean tryHard) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> configuredStepFeatures = world.getBiome(pos).getGenerationSettings().features();
        for (List<Supplier<ConfiguredFeature<?, ?>>> step: configuredStepFeatures)
            for (Supplier<ConfiguredFeature<?, ?>> provider: step) {
                ConfiguredFeature<?, ?> configuredFeature = provider.get();
                if (configuredFeature.feature == feature)
                    return configuredFeature;
            }
        if (!tryHard) return null;
        return world.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).entrySet().stream().
                filter(cS -> cS.getValue().feature == feature).
                findFirst().map(Map.Entry::getValue).orElse(null);
    }

    public static <T extends FeatureConfiguration> StructureStart shouldStructureStartAt(ServerLevel world, BlockPos pos, StructureFeature<T> structure, boolean computeBox) {
        long seed = world.getSeed();
        ChunkGenerator generator = world.getChunkSource().getGenerator();
        StructureFeatureConfiguration params = generator.getSettings().getConfig(structure);
        synchronized(boo) {
            if (!generator.getBiomeSource().hasStructureFeature(structure))
                return null;
        }
        BiomeManager biomeAccess = world.getBiomeManager().withDifferentSource(generator.getBiomeSource());
        ChunkRandom chunkRandom = new ChunkRandom();
        ChunkPos chunkPos = new ChunkPos(pos);
        Biome biome = biomeAccess.getBiome(new BlockPos(chunkPos.getMinBlockX() + 9, 0, chunkPos.getMinBlockZ() + 9));
        ConfiguredStructureFeature<?, ?> configuredFeature = biome.getGenerationSettings().method_30978(structure.configured(null));
        if (configuredFeature == null || configuredFeature.config == null) return null;
        ChunkPos chunkPos2 = structure.getPotentialFeatureChunk(params, seed, chunkRandom, chunkPos.x, chunkPos.z); //find some chunk I guess
        // using here world for heightview, rather than chunk since we - unlike vanilla, want to avoid creating any chunks even on the
        // structure starts level - lets see where would that take us.
        if (chunkPos.x == chunkPos2.x && chunkPos.z == chunkPos2.z && ((StructureFeatureInterface)structure).shouldStartPublicAt(generator, generator.getBiomeSource(), seed, chunkRandom, chunkPos, biome, chunkPos, configuredFeature.config, world)) // should start at {
            if (!computeBox) return StructureStart.INVALID_START;
            StructureManager manager = world.getStructureManager();
            StructureStart<T> structureStart3 = structure.getStructureStartFactory().create((StructureFeature<T>) configuredFeature.feature, chunkPos, 0, seed);
            synchronized (boo) {
                structureStart3.init(world.registryAccess(), generator, manager, chunkPos, biome, (T) configuredFeature.config, world);
            }
            if (!structureStart3.isValid()) return null;
            return structureStart3;
        }
        return null;
    }

    public static final Map<String, Thing> featureMap = new HashMap<String, Thing>() {{
        put("oak_bees", simpleTree(ConfiguredFeatures.OAK.getConfig().setTreeDecorators(List.of(new BeehiveDecorator(1.0F)))));
        put("fancy_oak_bees", simpleTree(ConfiguredFeatures.FANCY_OAK.getConfig().setTreeDecorators(List.of(new BeehiveDecorator(1.0F)))));
        put("birch_bees", simpleTree(ConfiguredFeatures.BIRCH.getConfig().setTreeDecorators(List.of(new BeehiveDecorator(1.0F)))));
        put("coral_tree", simplePlop(Feature.CORAL_TREE.configured(FeatureConfiguration.NONE)));
        put("coral_claw", simplePlop(Feature.CORAL_CLAW.configured(FeatureConfiguration.NONE)));
        put("coral_mushroom", simplePlop(Feature.CORAL_MUSHROOM.configured(FeatureConfiguration.NONE)));
        put("coral", simplePlop(Feature.SIMPLE_RANDOM_SELECTOR.configured(new SimpleRandomFeatureConfiguration(List.of(
                () -> Feature.CORAL_TREE.configure(FeatureConfig.DEFAULT),
                () -> Feature.CORAL_CLAW.configure(FeatureConfig.DEFAULT),
                () -> Feature.CORAL_MUSHROOM.configure(FeatureConfig.DEFAULT)
        )))));
        put("bastion_remnant_units", spawnCustomStructure(
                StructureFeature.BASTION_REMNANT,
                new JigsawConfiguration(() -> new StructureTemplatePool(
                        new ResourceLocation("bastion/starts"),
                        new ResourceLocation("empty"),
                        List.of(
                                Pair.of(StructurePoolElement.single("bastion/units/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)
                        ),
                        StructureTemplatePool.Projection.RIGID
                ), 6),
                Biomes.NETHER_WASTES
        ));
        put("bastion_remnant_hoglin_stable", spawnCustomStructure(
                StructureFeature.BASTION_REMNANT,
                new JigsawConfiguration(() -> new StructureTemplatePool(
                        new ResourceLocation("bastion/starts"),
                        new ResourceLocation("empty"),
                        List.of(
                                Pair.of(StructurePoolElement.single("bastion/hoglin_stable/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)
                        ),
                        StructureTemplatePool.Projection.RIGID
                ), 6),
                Biomes.NETHER_WASTES
        ));
        put("bastion_remnant_treasure", spawnCustomStructure(
                StructureFeature.BASTION_REMNANT,
                new JigsawConfiguration(() -> new StructureTemplatePool(
                        new ResourceLocation("bastion/starts"),
                        new ResourceLocation("empty"),
                        List.of(
                                Pair.of(StructurePoolElement.single("bastion/treasure/big_air_full", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)
                        ),
                        StructureTemplatePool.Projection.RIGID
                ), 6),
                Biomes.NETHER_WASTES
        ));
        put("bastion_remnant_bridge", spawnCustomStructure(
                StructureFeature.BASTION_REMNANT,
                new JigsawConfiguration(() -> new StructureTemplatePool(
                        new ResourceLocation("bastion/starts"),
                        new ResourceLocation("empty"),
                        List.of(
                                Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)
                        ),
                        StructureTemplatePool.Projection.RIGID
                ), 6),
                Biomes.NETHER_WASTES
        ));
    }};

}
