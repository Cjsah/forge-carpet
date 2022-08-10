package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(PistonStructureResolver.class)
public abstract class PistonHandler_customStickyMixin {
    /*
     * The following Mixins make double chests behave sticky on the side where they are connected to its other double chest half block.
     * This is achieved by Injecting calls to "stickToStickySide" where normally slimeblocks stick to all their neighboring blocks.
     * redirectGetBlockState_1_A/B is neccessary to get access to the blockState_1 variable, which is used in redirectSlimeBlock.
     * redirectSlimeBlock is neccessary to also enable chests to have the backward stickyness (this seems to be an edge case)
     *
     * Note that it is possible to separate chests the same way pistons can separate slimeblocks.
     *
     * These also support other custom sticky block with non-standard rules, like chains.
     */
    @Shadow @Final private Level level;
    @Shadow @Final private Direction pushDirection;
    @Shadow
    public abstract boolean addBlockLine(BlockPos blockPos_1, Direction direction_1);
    @Shadow
    public abstract boolean addBranchingBlocks(BlockPos pos);

    @Shadow @Final private List<BlockPos> toPush;
    @Inject(method = "resolve", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    /**
     * Handles blocks besides the slimeblock that are sticky.
     * Supports blocks that are sticky on one side, (double chests now, 2no2name)
     * as well as other special sticky blocks (chains, gnembon).
     * @author 2No2Name, gnembon
     */
    private void stickToStickySide(CallbackInfoReturnable<Boolean> cir, BlockState state, int int_1){
        if (CarpetSettings.movableBlockEntities) {
            if (!stickToStickySide(this.toPush.get(int_1))) {
                cir.setReturnValue(false);
            }
        }

        if (CarpetSettings.doChainStone) {
            BlockPos pos = this.toPush.get(int_1);
            BlockState chainState = level.getBlockState(pos);
            // chain is sideways
            if (chainState.getBlock() == Blocks.CHAIN && !isChainOnAxis(chainState, pushDirection)
                    && !this.addBranchingBlocks(pos)) {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Handles blocks besides the slimeblock that are sticky. Currently only supports blocks that are sticky on one side.
     * Currently the only additional sticky block is the double chest, which sticks to its other chest half.
     * @param blockPos_1 location of a block that moves and needs to stick other blocks to it
     * @author 2No2Name
     */
    private boolean stickToStickySide(BlockPos blockPos_1) {
        BlockState blockState_1 = this.level.getBlockState(blockPos_1);
        Block block = blockState_1.getBlock();
        Direction stickyDirection  = null;
        if(block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
            stickyDirection = getDirectionToOtherChestHalf(blockState_1);
        }

        //example how you could make sticky pistons have a sticky side:
        //else if(block == Blocks.STICKY_PISTON){
        //    stickyDirection = blockState_1.get(FacingBlock.FACING);
        //}

        return stickyDirection == null || this.addBlockLine(blockPos_1.relative(stickyDirection), stickyDirection);  //offset
    }

    /**
     * Custom movement of blocks stuck to the sides of blocks other than slimeblocks like chains
     */
    @Inject(method = "addBranchingBlocks", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            shift = At.Shift.BEFORE
    ))
    private void otherSideStickyCases(BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                      BlockState blockState, Direction var3[], int var4, int var5, Direction direction, BlockPos blockPos, BlockState blockState2) {
        if (CarpetSettings.doChainStone) {
            if (blockState.getBlock() == Blocks.CHAIN && isChainOnAxis(blockState, direction) && !blockState2.isAir()) {
                Block otherBlock = blockState2.getBlock();
                if ((otherBlock == Blocks.CHAIN && (blockState.getValue(ChainBlock.AXIS) == blockState2.getValue(ChainBlock.AXIS)))
                        || CarpetSettings.chainStoneStickToAll
                        || isEndRodOnAxis(blockState2, blockState.getValue(ChainBlock.AXIS))
                        || otherBlock == Blocks.HONEY_BLOCK
                        || Block.canSupportCenter(level, blockPos, direction.getOpposite())) {
                    if (!addBlockLine(blockPos, direction)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    /**
     * chains may cause moving adjacent blocks, meaning isAdjacentBlockStuck check is irreliable.
     * since initial block may not be sticky.
     * in vanilla canMoveAdjacent is always called on block2 being sticky.
     * @param block
     * @param block2
     * @return
     */
    @Redirect(method = "addBranchingBlocks", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean isStuckSlimeStone(BlockState block, BlockState block2) {
        return block2.isStickyBlock() && block.canStickTo(block2);
    }


    //if more helpers like this start existing, move this to Chest class
    /**
     * @param blockState blockState of one double chest half block
     * @return Direction towards the other block of the double chest, null if the blockState is not a double chest
     * @author 2No2Name
     */
    private Direction getDirectionToOtherChestHalf(BlockState blockState) {
        ChestType chestType;
        try{
            chestType = blockState.getValue(ChestBlock.TYPE);
        }catch(IllegalArgumentException e){return null;}
        if(chestType == ChestType.SINGLE)
            return null;
        return ChestBlock.getConnectedDirection(blockState);
    }


    private boolean isChainOnAxis(BlockState state, Direction stickDirection) {
        Direction.Axis axis;
        try {
            axis = state.getValue(ChainBlock.AXIS);
        }catch(IllegalArgumentException e){return false;}
        return stickDirection.getAxis() == axis;
    }

    private boolean isEndRodOnAxis(BlockState state, Direction.Axis stickAxis) {
        if (state.getBlock() != Blocks.END_ROD) return false;
        Direction facing;
        try {
            facing = state.getValue(EndRodBlock.FACING);
        }catch(IllegalArgumentException e){return false;}
        return stickAxis == facing.getAxis();
    }
}
