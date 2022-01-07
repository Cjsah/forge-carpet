package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandler_interactionUpdatesMixin
{
    @Inject(method = "onPlayerInteractBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
            shift = At.Shift.BEFORE
    ))
    private void beforeBlockInteracted(ServerboundUseItemOnPacket playerInteractBlockC2SPacket_1, CallbackInfo ci)
    {
        if (!CarpetSettings.interactionUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(true);
    }

    @Inject(method = "onPlayerInteractBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
            shift = At.Shift.AFTER
    ))
    private void afterBlockInteracted(ServerboundUseItemOnPacket playerInteractBlockC2SPacket_1, CallbackInfo ci)
    {
        if (!CarpetSettings.interactionUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(false);
    }

    @Inject(method = "onPlayerInteractItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactItem(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            shift = At.Shift.BEFORE
    ))
    private void beforeItemInteracted(ServerboundUseItemPacket packet, CallbackInfo ci)
    {
        if (!CarpetSettings.interactionUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(true);
    }

    @Inject(method = "onPlayerInteractItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactItem(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            shift = At.Shift.AFTER
    ))
    private void afterItemInteracted(ServerboundUseItemPacket packet, CallbackInfo ci)
    {
        if (!CarpetSettings.interactionUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(false);
    }
    @Inject(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;processBlockBreakingAction(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/Direction;I)V",
            shift = At.Shift.BEFORE
    ))
    private void beforeBlockBroken(ServerboundPlayerActionPacket packet, CallbackInfo ci)
    {
        if (!CarpetSettings.interactionUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(true);
    }

    @Inject(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target ="Lnet/minecraft/server/network/ServerPlayerInteractionManager;processBlockBreakingAction(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/Direction;I)V",
            shift = At.Shift.AFTER
    ))
    private void afterBlockBroken(ServerboundPlayerActionPacket packet, CallbackInfo ci)
    {
        if (!CarpetSettings.interactionUpdates)
            CarpetSettings.impendingFillSkipUpdates.set(false);
    }

}
