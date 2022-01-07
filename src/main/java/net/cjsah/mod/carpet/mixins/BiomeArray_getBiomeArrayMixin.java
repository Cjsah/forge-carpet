package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.BiomeArrayInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BiomeArray.class)
public class BiomeArray_getBiomeArrayMixin implements BiomeArrayInterface
{

    @Shadow @Final private static int HORIZONTAL_SECTION_COUNT;

    @Shadow @Final public static int HORIZONTAL_BIT_MASK;

    //@Shadow @Final public static int VERTICAL_BIT_MASK;

    @Shadow @Final private Biome[] data;

    @Shadow @Final private int field_28126;

    @Shadow @Final private int field_28127;

    @Override
    public void setBiomeAtIndex(BlockPos pos, Level world, Biome what)
    {
        int int_4 = QuartPos.fromBlock(pos.getX()) & HORIZONTAL_BIT_MASK;
        int int_5 = Mth.clamp(QuartPos.fromBlock(pos.getY())-field_28126, 0, field_28127);
        int int_6 = QuartPos.fromBlock(pos.getZ()) & HORIZONTAL_BIT_MASK;
        data[(int_5 << (HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT)) | (int_6 << HORIZONTAL_SECTION_COUNT) | int_4] = what;
    }
}
