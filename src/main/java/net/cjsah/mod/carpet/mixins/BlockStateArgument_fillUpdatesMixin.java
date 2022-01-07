package net.cjsah.mod.carpet.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockInput.class)
public class BlockStateArgument_fillUpdatesMixin
{
    @Redirect(method = "setBlockState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
    ))
    private BlockState postProcessStateProxy(BlockState state, LevelAccessor serverWorld, BlockPos blockPos)
    {
        if (CarpetSettings.impendingFillSkipUpdates.get())
        {
            return state;
        }
        
        return Block.updateFromNeighbourShapes(state, serverWorld, blockPos);
    }
}
