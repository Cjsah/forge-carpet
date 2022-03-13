package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.helpers.InventoryHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandler_stackableSBoxesMixin {
    @Redirect(method = "internalOnSlotClick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I"
    ))
    private int getMaxCountForSboxesInScreenHander(Slot slot, ItemStack stack) {
        if (CarpetSettings.stackableShulkerBoxes &&
                stack.getItem() instanceof BlockItem &&
                ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock &&
                !InventoryHelper.shulkerBoxHasItems(stack)
        ) {
            return CarpetSettings.SHULKER_STACK_SIZE;
        }
        return slot.getMaxStackSize(stack);
    }
}
