package net.cjsah.asm;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Test2 {
    public boolean setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft) {
        if (pFlags == 16 &&CarpetSettings.impendingFillSkipUpdates.get()) {
            pFlags = -1;
        }
        return !this.isOutsideBuildHeight(pPos);
    }
    boolean isOutsideBuildHeight(BlockPos pPos) {
        return false;
    }
}
