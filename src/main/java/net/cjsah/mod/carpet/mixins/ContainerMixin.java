package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMixin
{

    @Shadow public abstract void onSlotClick(int int_1, int int_2, ClickType slotActionType_1, Player playerEntity_1);
    
    @Shadow public abstract void sendContentUpdates();

    @Shadow @Final public NonNullList<Slot> slots;

    @Shadow protected abstract void endQuickCraft();

    @Shadow public abstract ItemStack getCursorStack();

    @Inject( method = "internalOnSlotClick", at = @At(value = "HEAD"), cancellable = true)
    private void onThrowClick(int slotId, int clickData, ClickType actionType, Player playerEntity, CallbackInfo ci)
    {
        if (actionType == ClickType.THROW && CarpetSettings.ctrlQCraftingFix && this.getCursorStack().isEmpty() && slotId >= 0)
        {
            ItemStack itemStack_1 = ItemStack.EMPTY;
            Slot slot_4 = slots.get(slotId);
            if (/*slot_4 != null && */slot_4.hasItem() && slot_4.mayPickup(playerEntity))
            {
                if(slotId == 0 && clickData == 1)
                {
                    Item craftedItem = slot_4.getItem().getItem();
                    while(slot_4.hasItem() && slot_4.getItem().getItem() == craftedItem)
                    {
                        this.onSlotClick(slotId, 0, ClickType.THROW, playerEntity);
                    }
                    this.sendContentUpdates();
                    this.endQuickCraft();
                    ci.cancel();
                }
            }
        }
    }
}
