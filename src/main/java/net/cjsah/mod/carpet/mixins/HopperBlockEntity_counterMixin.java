package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.helpers.HopperCounter;
import net.cjsah.mod.carpet.utils.WoolTool;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The {@link Mixin} which removes items in a hopper if it points into a wool counter, and calls {@link HopperCounter#add}
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntity_counterMixin extends RandomizableContainerBlockEntity
{
    protected HopperBlockEntity_counterMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow public abstract int getContainerSize();

    @Shadow public abstract void setItem(int slot, ItemStack stack);

    @Shadow
    private static boolean ejectItems(Level pLevel, BlockPos pPos, BlockState pState, HopperBlockEntity pSourceContainer) {
        return false;
    }

    /**
         * A method to remove items from hoppers pointing into wool and count them via {@link HopperCounter#add} method
         */


    @Redirect(method = "tryMoveItems", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;ejectItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;)Z"
    ))
    private static boolean onInsert(Level level, BlockPos pos, BlockState state, HopperBlockEntity hopper) {
        if (CarpetSettings.hopperCounters) {
            DyeColor wool_color = WoolTool.getWoolColorAtPosition(level, pos.relative(state.getValue(HopperBlock.FACING))); // offset
            if (wool_color != null) {
                for (int i = 0; i < hopper.getContainerSize(); ++i) {
                    if (!hopper.getItem(i).isEmpty()) {
                        ItemStack itemstack = hopper.getItem(i);//.copy();
                        HopperCounter.COUNTERS.get(wool_color).add(level.getServer(), itemstack);
                        hopper.setItem(i, ItemStack.EMPTY);
                    }
                }
                return true;
            }
        }
        return ejectItems(level, pos, state, hopper);
    }
}
