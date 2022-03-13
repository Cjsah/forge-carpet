package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.EntityInterface;
import net.cjsah.mod.carpet.fakes.ServerPlayerEntityInterface;
import net.cjsah.mod.carpet.script.EntityEventsGroup;
import com.mojang.authlib.GameProfile;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntity_scarpetEventMixin extends Player implements ServerPlayerEntityInterface {
    // to denote if the player reference is valid

    @Unique
    private boolean isInvalidReference = false;

    public ServerPlayerEntity_scarpetEventMixin(Level world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Shadow protected abstract void completeUsingItem();

    @Shadow public boolean notInAnyWorld;

    @Redirect(method = "consumeItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;consumeItem()V"
    ))
    private void finishedUsingItem(Player playerEntity) {
        if (CarpetEventServer.Event.PLAYER_FINISHED_USING_ITEM.isNeeded()) {
            InteractionHand hand = getUsedItemHand();
            CarpetEventServer.Event.PLAYER_FINISHED_USING_ITEM.onItemAction((ServerPlayer) (Object)this, hand, getUseItem());
            // do vanilla
            super.completeUsingItem();
        }
        else {
            // do vanilla
            super.completeUsingItem();
        }
    }

    @Inject(method = "increaseStat", at = @At("HEAD"))
    private void grabStat(Stat<?> stat, int amount, CallbackInfo ci) {
        CarpetEventServer.Event.STATISTICS.onPlayerStatistic((ServerPlayer) (Object)this, stat, amount);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathEvent(DamageSource source, CallbackInfo ci) {
        ((EntityInterface)this).getEventContainer().onEvent(EntityEventsGroup.Event.ON_DEATH, source.msgId);
        if (CarpetEventServer.Event.PLAYER_DIES.isNeeded()) {
            CarpetEventServer.Event.PLAYER_DIES.onPlayerEvent((ServerPlayer) (Object)this);
        }
    }

    @Redirect(method = "updateInput", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V"
    ))
    private void setSneakingConditionally(ServerPlayer serverPlayerEntity, boolean sneaking) {
        if (!((EntityInterface)serverPlayerEntity.getVehicle()).isPermanentVehicle()) // won't since that method makes sure its not null
            serverPlayerEntity.setShiftKeyDown(sneaking);
    }

    private Vec3 previousLocation;
    private ResourceKey<Level> previousDimension;

    @Inject(method = "moveToWorld", at = @At("HEAD"))
    private void logPreviousCoordinates(ServerLevel serverWorld, CallbackInfoReturnable<Entity> cir) {
        previousLocation = position();
        previousDimension = level.dimension();  //dimension type
    }

    @Inject(method = "moveToWorld", at = @At("RETURN"))
    private void atChangeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        if (CarpetEventServer.Event.PLAYER_CHANGES_DIMENSION.isNeeded()) {
            ServerPlayer player = (ServerPlayer) (Object)this;
            Vec3 to = null;
            if (!notInAnyWorld || previousDimension != Level.END || destination.dimension() != Level.OVERWORLD) // end ow {
                to = position();
            }
            CarpetEventServer.Event.PLAYER_CHANGES_DIMENSION.onDimensionChange(player, previousLocation, to, previousDimension, destination.dimension());
        }
    };

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

    @Inject(method = "setClientSettings", at = @At("HEAD"))
    public void setLanguage(ServerboundClientInformationPacket packet, CallbackInfo ci) {
        this.language = packet.getLanguage();
    }
}
