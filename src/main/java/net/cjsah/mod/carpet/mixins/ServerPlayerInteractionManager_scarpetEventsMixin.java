package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.ServerPlayerInteractionManagerInterface;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManager_scarpetEventsMixin implements ServerPlayerInteractionManagerInterface
{
    @Shadow public ServerPlayer player;

    @Shadow private boolean mining;

    @Shadow private BlockPos miningPos;

    @Shadow private int blockBreakingProgress;

    @Shadow public ServerLevel world;

    @Inject(method = "tryBreakBlock", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V",
            shift = At.Shift.BEFORE
    ))
    private void onBlockBroken(BlockPos blockPos_1, CallbackInfoReturnable<Boolean> cir, BlockState blockState_1, BlockEntity be, Block b)
    {
        CarpetEventServer.Event.PLAYER_BREAK_BLOCK.onBlockBroken(player, blockPos_1, blockState_1);
    }

    @Inject(method = "interactBlock", at = @At(
            value = "RETURN",
            ordinal = 2
    ))
    private void onBlockActivated(ServerPlayer serverPlayerEntity, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
    {
        CarpetEventServer.Event.PLAYER_INTERACTS_WITH_BLOCK.onBlockHit(player, hand, hitResult);
    }

    @Override
    public BlockPos getCurrentBreakingBlock()
    {
        if (!mining) return null;
        return miningPos;
    }

    @Override
    public int getCurrentBlockBreakingProgress()
    {
        if (!mining) return -1;
        return blockBreakingProgress;
    }

    @Override
    public void setBlockBreakingProgress(int progress)
    {
        blockBreakingProgress = Mth.clamp(progress, -1, 10);
        world.destroyBlockProgress(-1*this.player.getId(), miningPos, blockBreakingProgress);
    }
}
