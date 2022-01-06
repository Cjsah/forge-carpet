package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.script.CarpetEventServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Inventory.class)
public abstract class PlayerInventory_scarpetEventMixin
{
    @Shadow @Final public Player player;

    @Redirect(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(ILnet/minecraft/item/ItemStack;)Z"
    ))
    private boolean onItemAcquired(Inventory playerInventory, int slot, ItemStack stack)
    {
        if (!CarpetEventServer.Event.PLAYER_PICKS_UP_ITEM.isNeeded() || !(player instanceof ServerPlayer))
            return playerInventory.add(-1, stack);
        Item item = stack.getItem();
        int count = stack.getCount();
        boolean res = playerInventory.add(-1, stack);
        if (count != stack.getCount()) // res returns false for larger item adding to a almost full ineventory
        {
            ItemStack diffStack = new ItemStack(item, count - stack.getCount());
            diffStack.setTag(stack.getTag());
            CarpetEventServer.Event.PLAYER_PICKS_UP_ITEM.onItemAction((ServerPlayer) player, null, diffStack);
        }
        return res;
    }

}
