package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.helpers.BlockRotator;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HopperBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlock.class)
public class HopperBlock_cactusMixin
{
    @Redirect(method = "getPlacementState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemPlacementContext;getSide()Lnet/minecraft/util/math/Direction;"
    ))
    private Direction getOppositeOpposite(BlockPlaceContext context)
    {
        if (BlockRotator.flippinEligibility(context.getPlayer()))
        {
            return context.getClickedFace().getOpposite();
        }
        return context.getClickedFace();
    }
}
