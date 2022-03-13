package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.script.CarpetEventServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;


@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandler_scarpetEventsMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "onPlayerInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateInput(FFZZ)V"))
    private void checkMoves(ServerboundPlayerInputPacket p, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_RIDES.isNeeded() && (p.getXxa() != 0.0F || p.getZza() != 0.0F || p.isJumping() || p.isShiftKeyDown())) {
            CarpetEventServer.Event.PLAYER_RIDES.onMountControls(player, p.getXxa(), p.getZza(), p.isJumping(), p.isShiftKeyDown());
        }
    }

    @Inject(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;dropSelectedItem(Z)Z", // dropSelectedItem
            ordinal = 0,
            shift = At.Shift.BEFORE
    ))
    private void onQItem(ServerboundPlayerActionPacket playerActionC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_DROPS_ITEM.onPlayerEvent(player);
    }

    @Inject(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;",
            ordinal = 0,
            shift = At.Shift.BEFORE
    ))
    private void onHandSwap(ServerboundPlayerActionPacket playerActionC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_SWAPS_HANDS.onPlayerEvent(player);
    }

    @Inject(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;dropSelectedItem(Z)Z", // dropSelectedItem
            ordinal = 1,
            shift = At.Shift.BEFORE
    ))
    private void onCtrlQItem(ServerboundPlayerActionPacket playerActionC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_DROPS_STACK.onPlayerEvent(player);
    }


    @Inject(method = "onPlayerMove", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;jump()V"
    ))
    private void onJump(ServerboundMovePlayerPacket playerMoveC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_JUMPS.onPlayerEvent(player);
    }

    @Inject(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;processBlockBreakingAction(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/Direction;I)V",
            shift = At.Shift.BEFORE
    ))
    private void onClicked(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK)
            CarpetEventServer.Event.PLAYER_CLICKS_BLOCK.onBlockAction(player, packet.getPos(), packet.getDirection());
    }

    @Redirect(method = "onPlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;stopUsingItem()V"
    ))
    private void onStopUsing(ServerPlayer serverPlayerEntity) {
        if (CarpetEventServer.Event.PLAYER_RELEASED_ITEM.isNeeded()) {
            InteractionHand hand = serverPlayerEntity.getUsedItemHand();
            ItemStack stack = serverPlayerEntity.getUseItem().copy();
            serverPlayerEntity.releaseUsingItem();
            CarpetEventServer.Event.PLAYER_RELEASED_ITEM.onItemAction(player, hand, stack);
        }
        else {
            serverPlayerEntity.releaseUsingItem();
        }
    }

    @Inject(method = "onPlayerInteractBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
    ))
    private void onBlockInteracted(ServerboundUseItemOnPacket playerInteractBlockC2SPacket_1, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_RIGHT_CLICKS_BLOCK.isNeeded()) {
            InteractionHand hand = playerInteractBlockC2SPacket_1.getHand();
            BlockHitResult hitRes = playerInteractBlockC2SPacket_1.getHitResult();
            CarpetEventServer.Event.PLAYER_RIGHT_CLICKS_BLOCK.onBlockHit(player, hand, hitRes);
        }
    }

    @Inject(method = "onPlayerInteractItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"
    ))
    private void onItemClicked(ServerboundUseItemPacket playerInteractItemC2SPacket_1, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_USES_ITEM.isNeeded()) {
            InteractionHand hand = playerInteractItemC2SPacket_1.getHand();
            CarpetEventServer.Event.PLAYER_USES_ITEM.onItemAction(player, hand, player.getItemInHand(hand).copy());
        }
    }

    @Inject(method = "onClientCommand", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V",
            ordinal = 0
    ))
    private void onStartSneaking(ServerboundPlayerCommandPacket clientCommandC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_STARTS_SNEAKING.onPlayerEvent(player);
    }

    @Inject(method = "onClientCommand", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V",
            ordinal = 1
    ))
    private void onStopSneaking(ServerboundPlayerCommandPacket clientCommandC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_STOPS_SNEAKING.onPlayerEvent(player);
    }

    @Inject(method = "onClientCommand", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSprinting(Z)V",
            ordinal = 0
    ))
    private void onStartSprinting(ServerboundPlayerCommandPacket clientCommandC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_STARTS_SPRINTING.onPlayerEvent(player);
    }

    @Inject(method = "onClientCommand", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSprinting(Z)V",
            ordinal = 1
    ))
    private void onStopSprinting(ServerboundPlayerCommandPacket clientCommandC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_STOPS_SPRINTING.onPlayerEvent(player);
    }

    @Inject(method = "onClientCommand", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSleeping()Z",
            shift = At.Shift.BEFORE
    ))
    private void onWakeUp(ServerboundPlayerCommandPacket clientCommandC2SPacket_1, CallbackInfo ci) {
        //weird one - doesn't seem to work, maybe MP
        if (player.isSleeping())
            CarpetEventServer.Event.PLAYER_WAKES_UP.onPlayerEvent(player);
        else
            CarpetEventServer.Event.PLAYER_ESCAPES_SLEEP.onPlayerEvent(player);

    }

    @Inject(method = "onClientCommand", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;checkFallFlying()Z",
            shift = At.Shift.BEFORE
    ))
    private void onElytraEngage(ServerboundPlayerCommandPacket clientCommandC2SPacket_1, CallbackInfo ci) {
        CarpetEventServer.Event.PLAYER_DEPLOYS_ELYTRA.onPlayerEvent(player);
    }

    /*@Inject(method = "onPlayerInteractEntity", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;interact(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
    ))
    private void onEntityInteract(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket_1, CallbackInfo ci) {
        PLAYER_INTERACTS_WITH_ENTITY.onEntityHandAction(player, playerInteractEntityC2SPacket_1.getEntity(player.getServerWorld()), playerInteractEntityC2SPacket_1.getHand());
    }*/

    /*@Inject(method = "onPlayerInteractEntity", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"
    ))
    private void onEntityAttack(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket_1, CallbackInfo ci) {
        //todo add hit and hand in the future
        PLAYER_ATTACKS_ENTITY.onEntityHandAction(player, playerInteractEntityC2SPacket_1.getEntity(player.getServerWorld()), null);
    }*/

    @Inject(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"))
    private void onItemBeingPickedFromInventory(ServerboundContainerButtonClickPacket packet, CallbackInfo ci) {
        // crafts not int the crafting window
        //CarpetSettings.LOG.error("Player clicks button "+packet.getButtonId());
    }
    @Inject(method = "onCraftRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"))
    private void onRecipeSelectedInRecipeManager(ServerboundPlaceRecipePacket packet, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_CHOOSES_RECIPE.isNeeded()) {
            CarpetEventServer.Event.PLAYER_CHOOSES_RECIPE.onRecipeSelected(player, packet.getRecipe(), packet.isShiftDown());
        }
    }

    @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"))
    private void onUpdatedSelectedSLot(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_SWITCHES_SLOT.isNeeded() && player.getServer() != null && player.getServer().isSameThread()) {
            CarpetEventServer.Event.PLAYER_SWITCHES_SLOT.onSlotSwitch(player, player.getInventory().selected, packet.getSlot());
        }
    }

    @Inject(method = "onHandSwing", at = @At(
            value = "INVOKE", target =
            "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V",
            shift = At.Shift.BEFORE)
    )
    private void onSwing(ServerboundSwingPacket packet, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_SWINGS_HAND.isNeeded() && !player.swinging) {
            CarpetEventServer.Event.PLAYER_SWINGS_HAND.onHandAction(player, packet.getHand());
        }
    }
}
