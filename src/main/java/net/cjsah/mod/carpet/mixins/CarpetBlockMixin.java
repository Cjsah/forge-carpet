package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.utils.WoolTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WoolCarpetBlock.class) // WoolCarpetBlock
public abstract class CarpetBlockMixin extends Block {

    public CarpetBlockMixin(Properties block$Settings_1) {
        super(block$Settings_1);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (context.getPlayer() != null && !context.getLevel().isClientSide) { // getColor()
            WoolTool.carpetPlacedAction(((WoolCarpetBlock)(Object)this).getColor(), context.getPlayer(), context.getClickedPos(), (ServerLevel) context.getLevel());
        }
        return state;
    }
}
