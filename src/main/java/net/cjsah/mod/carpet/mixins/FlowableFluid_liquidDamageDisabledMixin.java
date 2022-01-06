package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public class FlowableFluid_liquidDamageDisabledMixin
{
    @Inject(
            method = "canFill",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Material;blocksMovement()Z"
            ),
            cancellable = true
    )
    private void stopBreakingBlock(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.liquidDamageDisabled)
        {
            Material material = state.getMaterial();
            cir.setReturnValue(material == Material.AIR || material.isLiquid());
        }
    }
}
