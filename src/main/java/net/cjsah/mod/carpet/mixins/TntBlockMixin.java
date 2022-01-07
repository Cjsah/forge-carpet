package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin
{
    // Add carpet rule check for tntDoNotUpdate to an if statement.
    @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean isTNTDoNotUpdate(Level world, BlockPos blockPos)
    {
        return !CarpetSettings.tntDoNotUpdate && world.hasNeighborSignal(blockPos);
    }
}
