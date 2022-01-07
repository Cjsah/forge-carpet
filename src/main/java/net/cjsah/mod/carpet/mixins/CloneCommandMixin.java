package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(CloneCommands.class)
public abstract class CloneCommandMixin
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private static int fillLimit(int original) {
        return CarpetSettings.fillLimit;
    }

    @Redirect(method = "execute", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;updateNeighbors(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"
    ))
    private static void conditionalUpdating(ServerLevel serverWorld, BlockPos blockPos_1, Block block_1)
    {
        if (CarpetSettings.fillUpdates) serverWorld.blockUpdated(blockPos_1, block_1);
    }
}
