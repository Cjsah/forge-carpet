package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.DesertPyramidFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@Mixin(DesertPyramidFeature.class)
public abstract class DesertPyramidFeatureMixin extends StructureFeature<NoneFeatureConfiguration> {
    private static final WeightedRandomList<MobSpawnSettings.SpawnerData> MONSTER_SPAWNS;
    
    static {
        MONSTER_SPAWNS = WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.HUSK, 1, 1, 1));
    }

    public DesertPyramidFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }


    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMonsterSpawns() {
        if (CarpetSettings.huskSpawningInTemples) {
            return MONSTER_SPAWNS;
        }
        return  MobSpawnSettings.EMPTY_MOB_LIST;
    }
}

