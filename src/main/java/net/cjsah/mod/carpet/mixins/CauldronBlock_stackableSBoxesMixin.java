package net.cjsah.mod.carpet.mixins;

import net.minecraft.world.level.block.CauldronBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CauldronBlock.class)
public class CauldronBlock_stackableSBoxesMixin {/*
    @Inject(method = "onUse", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;hasTag()Z",
            shift = At.Shift.BEFORE
    ))
    private void setSboxCount(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult arg5, CallbackInfoReturnable<Boolean> cir,
                              ItemStack itemStack, int i, Item item, Block block, ItemStack itemStack5) {
        if (CarpetSettings.stackableShulkerBoxes)
            itemStack5.setCount(itemStack.getCount());
    }*/
}
