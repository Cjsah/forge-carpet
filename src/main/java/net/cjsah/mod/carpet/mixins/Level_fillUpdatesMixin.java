package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public abstract class Level_fillUpdatesMixin
{
    @Redirect(method = "markAndNotifyBlock", at = @At( //setBlockState main
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;blockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"
    ))
    private void updateNeighborsMaybe(Level world, BlockPos blockPos, Block block)
    {
        if (!CarpetSettings.impendingFillSkipUpdates.get()) world.blockUpdated(blockPos, block);
    }

}
