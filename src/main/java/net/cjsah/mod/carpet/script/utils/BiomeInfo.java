package net.cjsah.mod.carpet.script.utils;

import net.cjsah.mod.carpet.script.value.ListValue;
import net.cjsah.mod.carpet.script.value.NumericValue;
import net.cjsah.mod.carpet.script.value.StringValue;
import net.cjsah.mod.carpet.script.value.Value;
import net.cjsah.mod.carpet.script.value.ValueConversions;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BiomeInfo {
    public final static Map<String, BiFunction<ServerLevel, Biome, Value>> biomeFeatures = new HashMap<String, BiFunction<ServerLevel, Biome, Value>>(){{
        //put("top_material", (w, b) -> new BlockValue( b.getGenerationSettings(). getSurfaceConfig().getTopMaterial(), null, null));
        //put("under_material", (w, b) -> new BlockValue( b.getGenerationSettings().getSurfaceConfig().getUnderMaterial(), null, null));
        put("category", (w, b) -> StringValue.of(Biome.getBiomeCategory(Holder.direct(b)).getName()));
        put("temperature", (w, b) -> NumericValue.of(b.getBaseTemperature()));
        put("fog_color", (w, b) -> ValueConversions.ofRGB(b.getSpecialEffects().getFogColor()));
        put("foliage_color", (w, b) -> ValueConversions.ofRGB(b.getSpecialEffects().getFoliageColorOverride().orElse(4764952))); // client Biome.getDefaultFoliageColor
        put("sky_color", (w, b) -> ValueConversions.ofRGB(b.getSpecialEffects().getSkyColor()));
        put("water_color", (w, b) -> ValueConversions.ofRGB(b.getSpecialEffects().getWaterColor()));
        put("water_fog_color", (w, b) -> ValueConversions.ofRGB(b.getSpecialEffects().getWaterFogColor()));
        put("humidity", (w, b) -> NumericValue.of(b.getDownfall()));
        put("precipitation", (w, b) -> StringValue.of(b.getPrecipitation().getName()));
        //put("depth", (w, b) -> NumericValue.of(b.getDepth()));
        //put("scale", (w, b) -> NumericValue.of(b.getScale()));
        put("features", (w, b) -> {

            Registry<ConfiguredFeature<?,?>> registry = w.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
            return ListValue.wrap(
                    b.getGenerationSettings().features().stream().map(step ->
                            ListValue.wrap(step.stream().map(cfp ->
                                    ValueConversions.of(registry.getKey(cfp.value().feature().value())))
                            )
                    )
            );
        });
        //put("structures", (w, b) -> {
        //    Registry<ConfiguredStructureFeature<?,?>> registry = w.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        //    return ListValue.wrap(b.getGenerationSettings().getStructureFeatures().stream().map(str -> ValueConversions.of(registry.getId(str.get()))));
        //});
    }};
}
