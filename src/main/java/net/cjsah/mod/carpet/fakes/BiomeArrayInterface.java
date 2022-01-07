package net.cjsah.mod.carpet.fakes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public interface BiomeArrayInterface
{
    void setBiomeAtIndex(BlockPos pos, Level world, Biome what);
}
