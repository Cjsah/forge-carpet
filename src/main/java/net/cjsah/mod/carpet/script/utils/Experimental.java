package net.cjsah.mod.carpet.script.utils;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import net.cjsah.mod.carpet.fakes.MinecraftServerInterface;
import net.cjsah.mod.carpet.fakes.ServerWorldInterface;
import net.cjsah.mod.carpet.script.CarpetScriptServer;
import net.cjsah.mod.carpet.script.value.ListValue;
import net.cjsah.mod.carpet.script.value.Value;
import net.cjsah.mod.carpet.script.value.ValueConversions;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Experimental
{
    public static Value reloadOne(MinecraftServer server)
    {
        LevelStorageSource.LevelStorageAccess session = ((MinecraftServerInterface) server).getCMSession();
        DataPackConfig dataPackSettings = session.getDataPacks();
        PackRepository resourcePackManager = server.getPackRepository();
        DataPackConfig dataPackSettings2 = MinecraftServer.configurePackRepository(resourcePackManager, dataPackSettings == null ? DataPackConfig.DEFAULT : dataPackSettings, false);

        CarpetScriptServer.LOG.error("datapacks: {}", dataPackSettings2.getEnabled());

        MinecraftServer.ReloadableResources serverRM = ((MinecraftServerInterface) server).getResourceManager();
        var resourceManager = serverRM.resourceManager();


        final RegistryAccess.Frozen currentRegistry = server.registryAccess();

        ImmutableList<PackResources> packsToLoad = resourcePackManager.getAvailableIds().stream().map(server.getPackRepository()::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList());
        final CloseableResourceManager resources = new MultiPackResourceManager(PackType.SERVER_DATA, packsToLoad);
        //ReloadableServerResources managers = ReloadableServerResources.loadResources(resources, currentRegistry, server.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, server.getFunctionCompilationLevel(), Util.backgroundExecutor(), Util.backgroundExecutor()).join();

        //believe the other one will fillup based on the datapacks only.
        //resources.close();


        //not sure its needed, but doesn't seem to have a negative effect and might be used in some custom shtuff
        //serverRM.updateGlobals();
        DynamicOps<Tag> dynamicOps = RegistryOps.create(NbtOps.INSTANCE, server.registryAccess());//, (ResourceManager) resourceManager);

        WorldData saveProperties = session.getDataTag(dynamicOps, dataPackSettings2, server.registryAccess().allElementsLifecycle());

        //RegistryReadOps<Tag> registryOps = RegistryReadOps.create(NbtOps.INSTANCE, serverRM.getResourceManager(), (RegistryAccess.RegistryHolder) server.registryAccess());
        //WorldData saveProperties = session.getDataTag(registryOps, dataPackSettings2);
        if (saveProperties == null) return Value.NULL;
        //session.backupLevelDataFile(server.getRegistryManager(), saveProperties); // no need

        // MinecraftServer.createWorlds
        // save properties should now contain dimension settings
        WorldGenSettings generatorOptions = saveProperties.worldGenSettings();
        boolean bl = generatorOptions.isDebug();
        long l = generatorOptions.seed();
        long m = BiomeManager.obfuscateSeed(l);
        Map<ResourceKey<Level>, ServerLevel> existing_worlds = ((MinecraftServerInterface) server).getCMWorlds();
        List<Value> addeds = new ArrayList<>();
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : generatorOptions.dimensions().entrySet())
        {
            ResourceKey<LevelStem> registryKey = entry.getKey();
            CarpetScriptServer.LOG.error("Analysing workld: {}", registryKey.location());
            if (!existing_worlds.containsKey(registryKey))
            {
                addeds.add(ValueConversions.of(registryKey.location()));
                ResourceKey<Level> registryKey2 = ResourceKey.create(Registry.DIMENSION_REGISTRY, registryKey.location());
                Holder<DimensionType> holder2 = (entry.getValue()).typeHolder();
                ChunkGenerator chunkGenerator3 = entry.getValue().generator();
                DerivedLevelData unmodifiableLevelProperties = new DerivedLevelData(saveProperties, ((ServerWorldInterface) server.overworld()).getWorldPropertiesCM());
                ServerLevel serverWorld2 = new ServerLevel(server, Util.backgroundExecutor(), session, unmodifiableLevelProperties, registryKey2, holder2, WorldTools.NOOP_LISTENER, chunkGenerator3, bl, m, List.of(), false);
                server.overworld().getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(serverWorld2.getWorldBorder()));
                existing_worlds.put(registryKey2, serverWorld2);
            }
        }
        return ListValue.wrap(addeds);
    }

    public static Value reloadTwo(MinecraftServer server)
    {
        LevelStorageSource.LevelStorageAccess session = ((MinecraftServerInterface)server).getCMSession();
        DataPackConfig dataPackSettings = session.getDataPacks();
        PackRepository resourcePackManager = server.getPackRepository();

        WorldStem.InitConfig initConfig = new WorldStem.InitConfig(resourcePackManager, Commands.CommandSelection.DEDICATED, 4, false);


        final WorldStem data = WorldStem.load(initConfig, () -> {
                    DataPackConfig dataPackConfig = session.getDataPacks();
                    return dataPackConfig == null ? DataPackConfig.DEFAULT : dataPackConfig;
                },
                (resourceManager, dataPackConfigx) -> {
                    RegistryAccess.Writable writable = RegistryAccess.builtinCopy();
                    DynamicOps<Tag> dynamicOps = RegistryOps.createAndLoad(NbtOps.INSTANCE, writable, (ResourceManager) resourceManager);
                    WorldData worldData = session.getDataTag(dynamicOps, dataPackConfigx, writable.allElementsLifecycle());
                    return Pair.of(worldData, writable.freeze());
                }, Util.backgroundExecutor(), Runnable::run).join();
        WorldGenSettings generatorOptions = data.worldData().worldGenSettings();

        boolean bl = generatorOptions.isDebug();
        long l = generatorOptions.seed();
        long m = BiomeManager.obfuscateSeed(l);
        Map<ResourceKey<Level>, ServerLevel> existing_worlds = ((MinecraftServerInterface)server).getCMWorlds();
        List<Value> addeds = new ArrayList<>();
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : generatorOptions.dimensions().entrySet()) {
            ResourceKey<LevelStem> registryKey = entry.getKey();
            CarpetScriptServer.LOG.error("Analysing workld: {}", registryKey.location());
            if (!existing_worlds.containsKey(registryKey))
            {
                ResourceKey<Level> resourceKey2 = ResourceKey.create(Registry.DIMENSION_REGISTRY, registryKey.location());
                DerivedLevelData derivedLevelData = new DerivedLevelData(data.worldData(), ((ServerWorldInterface) server.overworld()).getWorldPropertiesCM());
                ChunkGenerator chunkGenerator2 = ((LevelStem)entry.getValue()).generator();
                Holder<DimensionType> holder2 = ((LevelStem)entry.getValue()).typeHolder();
                ServerLevel serverLevel2 = new ServerLevel(server, Util.backgroundExecutor(), session, derivedLevelData, resourceKey2,
                        holder2, WorldTools.NOOP_LISTENER,chunkGenerator2, bl, m, ImmutableList.of(), false);
                server.overworld().getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(serverLevel2.getWorldBorder()));
                existing_worlds.put(resourceKey2, serverLevel2);
                addeds.add(ValueConversions.of(registryKey.location()));
            }
        }
        ((MinecraftServerInterface)server).reloadAfterReload(data.registryAccess());
        return ListValue.wrap(addeds);
    }
}
