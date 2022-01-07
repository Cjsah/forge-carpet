package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlock_stackableSBoxesMixin
{
    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/cauldron/CauldronBehavior;interact(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;"))
    private InteractionResult wrapInteractor(CauldronInteraction cauldronBehavior, BlockState blockState, Level world, BlockPos blockPos, Player playerEntity, InteractionHand hand, ItemStack itemStack)
    {
        int count = -1;
        if (CarpetSettings.stackableShulkerBoxes && itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            count = itemStack.getCount();
        InteractionResult result = cauldronBehavior.interact(blockState, world, blockPos, playerEntity, hand, itemStack);
        if (count > 0 && result.consumesAction())
        {
            ItemStack current = playerEntity.getItemInHand(hand);
            if (current.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock)
                current.setCount(count);
        }
        return result;
    }
}
