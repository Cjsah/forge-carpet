package net.cjsah.mod.carpet.mixins;

import com.mojang.authlib.GameProfile;
import net.cjsah.mod.carpet.fakes.EntityInterface;
import net.cjsah.mod.carpet.fakes.ServerPlayerEntityInterface;
import net.cjsah.mod.carpet.script.EntityEventsGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cjsah.mod.carpet.script.CarpetEventServer.Event.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayer_scarpetEventMixin extends Player implements ServerPlayerEntityInterface {
    // to denote if the player reference is valid

    @Unique
    private boolean isInvalidReference = false;

    public ServerPlayer_scarpetEventMixin(Level world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Shadow protected abstract void completeUsingItem();

    @Redirect(method = "completeUsingItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;completeUsingItem()V"
    ))
    private void finishedUsingItem(Player playerEntity) {
        if (PLAYER_FINISHED_USING_ITEM.isNeeded()) {
            InteractionHand hand = getUsedItemHand();
            PLAYER_FINISHED_USING_ITEM.onItemAction((ServerPlayer) (Object)this, hand, getUseItem());
            // do vanilla
            super.completeUsingItem();
        }
        else {
            // do vanilla
            super.completeUsingItem();
        }
    }

    @Inject(method = "awardStat", at = @At("HEAD"))
    private void grabStat(Stat<?> stat, int amount, CallbackInfo ci) {
        STATISTICS.onPlayerStatistic((ServerPlayer) (Object)this, stat, amount);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeathEvent(DamageSource source, CallbackInfo ci) {
        ((EntityInterface)this).getEventContainer().onEvent(EntityEventsGroup.Event.ON_DEATH, source.msgId);
        if (PLAYER_DIES.isNeeded()) {
            PLAYER_DIES.onPlayerEvent((ServerPlayer) (Object)this);
        }
    }

    @Redirect(method = "setPlayerInput", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V"
    ))
    private void setSneakingConditionally(ServerPlayer serverPlayerEntity, boolean sneaking) {
        if (!((EntityInterface)serverPlayerEntity.getVehicle()).isPermanentVehicle()) // won't since that method makes sure its not null
            serverPlayerEntity.setShiftKeyDown(sneaking);
    }

    @Override
    public void invalidateEntityObjectReference() {
        isInvalidReference = true;
    }

    @Override
    public boolean isInvalidEntityObject() {
        return isInvalidReference;
    }

    //getting player language
    @Unique
    private String language;

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Inject(method = "updateOptions", at = @At("HEAD"))
    public void setLanguage(ServerboundClientInformationPacket packet, CallbackInfo ci) {
        this.language = packet.language();
    }
}
