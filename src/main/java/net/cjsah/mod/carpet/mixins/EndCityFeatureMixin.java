package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.EndCityFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@Mixin(EndCityFeature.class)
public abstract class EndCityFeatureMixin extends StructureFeature<NoneFeatureConfiguration> {
    private static final WeightedRandomList<MobSpawnSettings.SpawnerData> spawnList = WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.SHULKER, 10, 4, 4));

    public EndCityFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMonsterSpawns() {
        if (CarpetSettings.shulkerSpawningInEndCities)
            return spawnList;
        return  MobSpawnSettings.EMPTY_MOB_LIST;
    }
}
