package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.EntityInterface;
import net.cjsah.mod.carpet.script.EntityEventsGroup;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class PlayerEntity_scarpetEventsMixin extends LivingEntity {
    protected PlayerEntity_scarpetEventsMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "applyDamage", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"
    ))
    private void playerTakingDamage(DamageSource source, float amount, CallbackInfo ci) {
        // version of LivingEntity_scarpetEventsMixin::entityTakingDamage
        ((EntityInterface)this).getEventContainer().onEvent(EntityEventsGroup.Event.ON_DAMAGE, amount, source);
        if (CarpetEventServer.Event.PLAYER_TAKES_DAMAGE.isNeeded()) {
            CarpetEventServer.Event.PLAYER_TAKES_DAMAGE.onDamage(this, amount, source);
        }
        if (source.getEntity() instanceof ServerPlayer && CarpetEventServer.Event.PLAYER_DEALS_DAMAGE.isNeeded()) {
            CarpetEventServer.Event.PLAYER_DEALS_DAMAGE.onDamage(this, amount, source);
        }
    }

    @Inject(method = "collideWithEntity", at = @At("HEAD"))
    private void onEntityCollision(Entity entity, CallbackInfo ci) {
        if (CarpetEventServer.Event.PLAYER_COLLIDES_WITH_ENTITY.isNeeded() && !level.isClientSide) {
            CarpetEventServer.Event.PLAYER_COLLIDES_WITH_ENTITY.onEntityHandAction((ServerPlayer)(Object)this, entity, null);
        }
    }

    @Inject(method = "interact", at = @At("HEAD"))
    private void doInteract(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.isClientSide && CarpetEventServer.Event.PLAYER_INTERACTS_WITH_ENTITY.isNeeded()) {
            CarpetEventServer.Event.PLAYER_INTERACTS_WITH_ENTITY.onEntityHandAction((ServerPlayer) (Object)this, entity, hand);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        if (!level.isClientSide && CarpetEventServer.Event.PLAYER_ATTACKS_ENTITY.isNeeded() && target.isAttackable()) {
            CarpetEventServer.Event.PLAYER_ATTACKS_ENTITY.onEntityHandAction((ServerPlayer) (Object)this, target, null);
        }
    }
}
