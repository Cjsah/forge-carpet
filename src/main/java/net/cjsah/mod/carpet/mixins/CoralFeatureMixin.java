package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.CoralFeatureInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;

@Mixin(CoralFeature.class)
public abstract class CoralFeatureMixin implements CoralFeatureInterface {

    @Shadow protected abstract boolean generateCoral(LevelAccessor var1, Random var2, BlockPos var3, BlockState var4);

    @Override
    public boolean growSpecific(Level worldIn, Random random, BlockPos pos, BlockState blockUnder) {
        return generateCoral(worldIn, random, pos, blockUnder);
    }
}
