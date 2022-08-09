package net.cjsah.mod.carpet.fakes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public interface CoralFeatureInterface {
    boolean growSpecific(Level worldIn, Random random, BlockPos pos, BlockState blockUnder);
}
