package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class FluidBlock_renewableDeepslateMixin {

    @Shadow protected abstract void playExtinguishSound(LevelAccessor world, BlockPos pos);

    @Inject(method = "receiveNeighborFluids", at = @At(value = "INVOKE",target = "Lnet/minecraft/fluid/FluidState;isStill()Z"), cancellable = true)
    private void receiveFluidToDeepslate(Level world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        if(CarpetSettings.renewableDeepslate && !world.getFluidState(pos).isSource() && world.dimension() == Level.OVERWORLD && pos.getY() < 16)
        {
            world.setBlockAndUpdate(pos, Blocks.COBBLED_DEEPSLATE.defaultBlockState());
            this.playExtinguishSound(world, pos);
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
