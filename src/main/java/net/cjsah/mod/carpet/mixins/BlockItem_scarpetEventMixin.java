package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.script.CarpetEventServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;

@Mixin(BlockItem.class)
public class BlockItem_scarpetEventMixin
{
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V",
            shift = At.Shift.AFTER
    ))
    private void afterPlacement(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir)
    {
        if (context.getPlayer() instanceof ServerPlayer && CarpetEventServer.Event.PLAYER_PLACES_BLOCK.isNeeded())
            CarpetEventServer.Event.PLAYER_PLACES_BLOCK.onBlockPlaced((ServerPlayer) context.getPlayer(), context.getClickedPos(), context.getHand(), context.getItemInHand());
    }
}
