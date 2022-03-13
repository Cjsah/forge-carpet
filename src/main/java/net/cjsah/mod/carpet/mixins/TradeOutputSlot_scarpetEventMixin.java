package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.script.CarpetEventServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;

@Mixin(value = MerchantResultSlot.class)
public abstract class TradeOutputSlot_scarpetEventMixin {
    @Shadow @Final private Merchant merchant;

    @Shadow @Final private MerchantContainer merchantInventory;

    @Inject(method = "onTakeItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/village/Merchant;trade(Lnet/minecraft/village/TradeOffer;)V")
    )
    private void onTrade(Player player, ItemStack stack, CallbackInfo ci) {
        if(CarpetEventServer.Event.PLAYER_TRADES.isNeeded() && !this.merchant.getMerchantWorld().isClient()) {
            CarpetEventServer.Event.PLAYER_TRADES.onTrade((ServerPlayer) player, merchant, merchantInventory.getActiveOffer());
        }
    }
}
