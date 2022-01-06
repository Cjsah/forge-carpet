package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.BiomeEffectsInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

@Mixin(BiomeSpecialEffects.class)
public class BiomeEffects_scarpetMixin implements BiomeEffectsInterface
{
    @Shadow @Final private int fogColor;

    @Shadow @Final private Optional<Integer> foliageColor;

    @Shadow @Final private int skyColor;

    @Shadow @Final private int waterColor;

    @Shadow @Final private int waterFogColor;

    @Override
    public int getCMFogColor()
    {
        return fogColor;
    }

    @Override
    public Optional<Integer> getCMFoliageColor()
    {
        return foliageColor;
    }

    @Override
    public int getCMSkyColor()
    {
        return skyColor;
    }

    @Override
    public int getCMWaterColor()
    {
        return waterColor;
    }

    @Override
    public int getCMWaterFogColor()
    {
        return waterFogColor;
    }
}
