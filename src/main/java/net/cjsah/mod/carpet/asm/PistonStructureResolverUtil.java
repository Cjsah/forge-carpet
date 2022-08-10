package net.cjsah.mod.carpet.asm;

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

@SuppressWarnings("unused")
public class PistonStructureResolverUtil {
    public static boolean blockCanBePulled(BlockState blockState, Direction direction) {
        if (CarpetSettings.movableBlockEntities) {
            Block block = blockState.getBlock();
            if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)
                //Make chests be sticky on the side to
                return getDirectionToOtherChestHalf(blockState) == direction.getOpposite();
            //example how you could make sticky pistons have a sticky side:
            //if(block == Blocks.STICKY_PISTON)
            //    return blockState.get(FacingBlock.FACING) == motionDirection;
        }
        if (CarpetSettings.doChainStone && blockState.getBlock() == Blocks.CHAIN) {
            return isChainOnAxis(blockState, direction);
        }


        return false;
    }

    public static boolean isDraggingPreviousBlockBehind(BlockState previous, BlockState next, Level level, Direction direction, BlockPos pos) {
        if (CarpetSettings.doChainStone) {
            if (previous.getBlock() == Blocks.CHAIN && isChainOnAxis(previous, direction)) {
                if ( (next.getBlock() == Blocks.CHAIN && isChainOnAxis(next, direction))
                        || CarpetSettings.chainStoneStickToAll
                        || isEndRodOnAxis(next, direction.getAxis())
                        || Block.canSupportCenter(level, pos, direction)) {
                    return true;
                }
            }
        }
        return previous.canStickTo(next);
    }

    public static boolean stickToStickySide(PistonStructureResolver resolver, Level level, BlockPos pos) {
        if (CarpetSettings.movableBlockEntities) {
            BlockState blockState_1 = level.getBlockState(pos);
            Block block = blockState_1.getBlock();
            Direction stickyDirection  = null;
            if(block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                stickyDirection = getDirectionToOtherChestHalf(blockState_1);
            }

            //example how you could make sticky pistons have a sticky side:
            //else if(block == Blocks.STICKY_PISTON){
            //    stickyDirection = blockState_1.get(FacingBlock.FACING);
            //}

            //offset
            return !(stickyDirection == null || resolver.addBlockLine(pos.relative(stickyDirection), stickyDirection));
        }
        return false;

    }

    public static boolean redirectIsStickyBlock(PistonStructureResolver resolver, BlockState chainState, BlockPos blockPos3, Direction direction) {
        if (CarpetSettings.doChainStone) {
            return chainState.getBlock() == Blocks.CHAIN && !isChainOnAxis(chainState, direction) && !resolver.addBranchingBlocks(blockPos3);
        }
        return false;
    }


    private static Direction getDirectionToOtherChestHalf(BlockState blockState) {
        ChestType chestType;
        try{
            chestType = blockState.getValue(ChestBlock.TYPE);
        }catch(IllegalArgumentException e){return null;}
        if(chestType == ChestType.SINGLE)
            return null;
        return ChestBlock.getConnectedDirection(blockState);
    }

    private static boolean isChainOnAxis(BlockState state, Direction stickDirection) {
        Direction.Axis axis;
        try {
            axis = state.getValue(ChainBlock.AXIS);
        }catch(IllegalArgumentException e){return false;}
        return stickDirection.getAxis() == axis;
    }

    private static boolean isEndRodOnAxis(BlockState state, Direction.Axis stickAxis) {
        if (state.getBlock() != Blocks.END_ROD) return false;
        Direction facing;
        try {
            facing = state.getValue(EndRodBlock.FACING);
        }catch(IllegalArgumentException e){return false;}
        return stickAxis == facing.getAxis();
    }

}
