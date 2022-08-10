package net.cjsah.asm;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;

public class Test4 {
    private final Level level = null;
    private final BlockPos pistonPos= BlockPos.ZERO;
    private final Direction pushDirection = Direction.UP;
    private final List<BlockPos> toPush = Lists.newArrayList();
    private final List<BlockPos> toDestroy = Lists.newArrayList();

    private boolean addBlockLine(BlockPos pOriginPos, Direction pDirection) {
        BlockState blockstate = this.level.getBlockState(pOriginPos);
        if (level.isEmptyBlock(pOriginPos)) {
            return true;
        } else if (!PistonBaseBlock.isPushable(blockstate, this.level, pOriginPos, this.pushDirection, false, pDirection)) {
            return true;
        } else if (pOriginPos.equals(this.pistonPos)) {
            return true;
        } else if (this.toPush.contains(pOriginPos)) {
            return true;
        } else {
            int i = 1;
            if (i + this.toPush.size() > 12) {
                return false;
            } else {
                BlockState oldState;
                while(blockstate.isStickyBlock()) {
                    BlockPos blockpos = pOriginPos.relative(this.pushDirection.getOpposite(), i);
                    oldState = blockstate;
                    blockstate = this.level.getBlockState(blockpos);
                    if (blockstate.isAir() || !oldState.canStickTo(blockstate) || !PistonBaseBlock.isPushable(blockstate, this.level, blockpos, this.pushDirection, false, this.pushDirection.getOpposite()) || blockpos.equals(this.pistonPos)) {
                        break;
                    }

                    ++i;
                    if (i + this.toPush.size() > 12) {
                        return false;
                    }
                }

                int l = 0;

                for(int i1 = i - 1; i1 >= 0; --i1) {
                    this.toPush.add(pOriginPos.relative(this.pushDirection.getOpposite(), i1));
                    ++l;
                }

                int j1 = 1;

                while(true) {
                    BlockPos blockpos1 = pOriginPos.relative(this.pushDirection, j1);
                    int j = this.toPush.indexOf(blockpos1);
                    if (j > -1) {
                        this.reorderListAtCollision(l, j);

                        for(int k = 0; k <= j + l; ++k) {
                            BlockPos blockpos2 = this.toPush.get(k);
                            if (this.level.getBlockState(blockpos2).isStickyBlock() && !this.addBranchingBlocks(blockpos2)) {
                                return false;
                            }
                        }

                        return true;
                    }

                    blockstate = this.level.getBlockState(blockpos1);
                    if (blockstate.isAir()) {
                        return true;
                    }

                    if (!PistonBaseBlock.isPushable(blockstate, this.level, blockpos1, this.pushDirection, true, this.pushDirection) || blockpos1.equals(this.pistonPos)) {
                        return false;
                    }

                    if (blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
                        this.toDestroy.add(blockpos1);
                        return true;
                    }

                    if (this.toPush.size() >= 12) {
                        return false;
                    }

                    this.toPush.add(blockpos1);
                    ++l;
                    ++j1;
                }
            }
        }
    }

    private void reorderListAtCollision(int pOffsets, int pIndex) {
    }
    private boolean addBranchingBlocks(BlockPos pFromPos) {
        return true;
    }

}
