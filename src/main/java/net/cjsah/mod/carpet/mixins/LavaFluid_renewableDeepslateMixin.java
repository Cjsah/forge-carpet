package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class LavaFluid_renewableDeepslateMixin {
    @Shadow protected abstract void playExtinguishEvent(LevelAccessor world, BlockPos pos);

    @Inject(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;"), cancellable = true)
    private void generateDeepslate(LevelAccessor world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci)
    {
        if(CarpetSettings.renewableDeepslate && ((Level)world).dimension() == Level.OVERWORLD && pos.getY() < 16)
        {
            world.setBlock(pos, Blocks.DEEPSLATE.defaultBlockState(), 3);
            this.playExtinguishEvent(world, pos);
            ci.cancel();
        }
    }
}
