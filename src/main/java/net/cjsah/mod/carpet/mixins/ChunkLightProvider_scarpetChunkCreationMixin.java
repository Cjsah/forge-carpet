package net.cjsah.mod.carpet.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.cjsah.mod.carpet.fakes.ChunkLightProviderInterface;
import net.cjsah.mod.carpet.fakes.Lighting_scarpetChunkCreationInterface;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

@Mixin(LayerLightEngine.class)
public abstract class ChunkLightProvider_scarpetChunkCreationMixin implements Lighting_scarpetChunkCreationInterface, ChunkLightProviderInterface
{
    @Shadow
    @Final
    protected LayerLightSectionStorage<?> lightStorage;

    @Override
    public void removeLightData(final long pos)
    {
        ((Lighting_scarpetChunkCreationInterface) this.lightStorage).removeLightData(pos);
    }

    @Override
    public void relight(final long pos)
    {
        ((Lighting_scarpetChunkCreationInterface) this.lightStorage).relight(pos);
    }

    @Override
    @Invoker("getCurrentLevelFromSection")
    public abstract int callGetCurrentLevelFromSection(DataLayer array, long blockPos);
}
