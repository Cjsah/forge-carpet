package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.helpers.BlockRotator;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UseOnContext.class)
public class ItemUsageContext_cactusMixin
{
    @Redirect(method = "getPlayerFacing", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getHorizontalFacing()Lnet/minecraft/util/math/Direction;"
    ))
    private Direction getPlayerFacing(Player playerEntity)
    {
        Direction dir = playerEntity.getDirection();
        if (BlockRotator.flippinEligibility(playerEntity))
        {
            dir = dir.getOpposite();
        }
        return dir;
    }
}
