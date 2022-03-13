package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.BastionFeature;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

@Mixin(BastionFeature.class)
public abstract class BastionRemnantFeatureMixin extends JigsawFeature {
    private static final WeightedRandomList<MobSpawnSettings.SpawnerData> spawnList = WeightedRandomList.create(
            new MobSpawnSettings.SpawnerData(EntityType.PIGLIN_BRUTE, 5, 1, 2),
            new MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 10, 2, 4),
            new MobSpawnSettings.SpawnerData(EntityType.HOGLIN, 2, 1, 2)
    );

    public BastionRemnantFeatureMixin(Codec<JigsawConfiguration> codec, int i, boolean bl, boolean bl2) {
        super(codec, i, bl, bl2);
    }

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMonsterSpawns() {
        if (CarpetSettings.piglinsSpawningInBastions)
            return spawnList;
        return MobSpawnSettings.EMPTY_MOB_LIST;
    }
}
