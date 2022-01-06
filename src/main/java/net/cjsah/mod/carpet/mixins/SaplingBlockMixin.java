package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.helpers.BlockSaplingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin
{
    @Inject(method = "generate", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/block/sapling/SaplingGenerator;generate(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Ljava/util/Random;)Z"),
            cancellable = true)
    private void onGenerate(ServerLevel serverWorld_1, BlockPos blockPos_1, BlockState blockState_1, Random random_1, CallbackInfo ci)
    {
        if(CarpetSettings.desertShrubs && serverWorld_1.getBiome(blockPos_1).getBiomeCategory() == Biome.BiomeCategory.DESERT && !BlockSaplingHelper.hasWater(serverWorld_1, blockPos_1))
        {
            serverWorld_1.setBlock(blockPos_1, Blocks.DEAD_BUSH.defaultBlockState(), 3);
            ci.cancel();
        }
    }
}
