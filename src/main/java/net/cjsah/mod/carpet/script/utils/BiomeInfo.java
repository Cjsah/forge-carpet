package net.cjsah.mod.carpet.script.utils;

import net.cjsah.mod.carpet.fakes.BiomeEffectsInterface;
import net.cjsah.mod.carpet.script.value.BlockValue;
import net.cjsah.mod.carpet.script.value.ListValue;
import net.cjsah.mod.carpet.script.value.NumericValue;
import net.cjsah.mod.carpet.script.value.StringValue;
import net.cjsah.mod.carpet.script.value.Value;
import net.cjsah.mod.carpet.script.value.ValueConversions;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public class BiomeInfo {
    public final static Map<String, BiFunction<ServerLevel, Biome, Value>> biomeFeatures = new HashMap<String, BiFunction<ServerLevel, Biome, Value>>(){{
        put("top_material", (w, b) -> new BlockValue( b.getGenerationSettings().getSurfaceConfig().getTopMaterial(), null, null));
        put("under_material", (w, b) -> new BlockValue( b.getGenerationSettings().getSurfaceConfig().getUnderMaterial(), null, null));
        put("category", (w, b) -> StringValue.of(b.getBiomeCategory().getName()));
        put("temperature", (w, b) -> NumericValue.of(b.getBaseTemperature()));
        put("fog_color", (w, b) -> ValueConversions.ofRGB(((BiomeEffectsInterface)b.getSpecialEffects()).getCMFogColor()));
        put("foliage_color", (w, b) -> ValueConversions.ofRGB(((BiomeEffectsInterface)b.getSpecialEffects()).getCMFoliageColor().orElse(4764952))); // client Biome.getDefaultFoliageColor
        put("sky_color", (w, b) -> ValueConversions.ofRGB(((BiomeEffectsInterface)b.getSpecialEffects()).getCMSkyColor()));
        put("water_color", (w, b) -> ValueConversions.ofRGB(((BiomeEffectsInterface)b.getSpecialEffects()).getCMWaterColor()));
        put("water_fog_color", (w, b) -> ValueConversions.ofRGB(((BiomeEffectsInterface)b.getSpecialEffects()).getCMWaterFogColor()));
        put("humidity", (w, b) -> NumericValue.of(b.getDownfall()));
        put("precipitation", (w, b) -> StringValue.of(b.getPrecipitation().getName()));
        put("depth", (w, b) -> NumericValue.of(b.getDepth()));
        put("scale", (w, b) -> NumericValue.of(b.getScale()));
        put("features", (w, b) -> {

            Registry<ConfiguredFeature<?,?>> registry = w.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
            return ListValue.wrap(
                    b.getGenerationSettings().features().stream().map(step ->
                            ListValue.wrap(step.stream().map(cfp ->
                                    ValueConversions.of(registry.getKey(cfp.get()))
                            ))
                    )
            );
        });
        put("structures", (w, b) -> {
            Registry<ConfiguredStructureFeature<?,?>> registry = w.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
            return ListValue.wrap(b.getGenerationSettings().getStructureFeatures().stream().map(str -> ValueConversions.of(registry.getId(str.get()))));
        });
    }};
}
