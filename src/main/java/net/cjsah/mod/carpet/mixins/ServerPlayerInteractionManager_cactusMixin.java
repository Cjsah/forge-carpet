package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.helpers.BlockRotator;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManager_cactusMixin
{

    @Redirect(method = "interactBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
    ))
    private InteractionResult activateWithOptionalCactus(BlockState blockState, Level world_1, Player playerEntity_1, InteractionHand hand_1, BlockHitResult blockHitResult_1)
    {
        boolean flipped = BlockRotator.flipBlockWithCactus(blockState, world_1, playerEntity_1, hand_1, blockHitResult_1);
        if (flipped)
            return InteractionResult.SUCCESS;

        return blockState.use(world_1, playerEntity_1, hand_1, blockHitResult_1);
    }
}
