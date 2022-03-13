package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerEntity_creativeNoClipMixin extends LivingEntity {
    protected PlayerEntity_creativeNoClipMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Redirect(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z")
    )
    private boolean canClipTroughWorld(Player playerEntity) {
        return playerEntity.isSpectator() || (CarpetSettings.creativeNoClip && playerEntity.isCreative() && playerEntity.getAbilities().flying);

    }

    @Redirect(method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z")
    )
    private boolean collidesWithEntities(Player playerEntity) {
        return playerEntity.isSpectator() || (CarpetSettings.creativeNoClip && playerEntity.isCreative() && playerEntity.getAbilities().flying);
    }

    @Redirect(method = "updatePose", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z")
    )
    private boolean spectatorsDontPose(Player playerEntity) {
        return playerEntity.isSpectator() || (CarpetSettings.creativeNoClip && playerEntity.isCreative() && playerEntity.getAbilities().flying);
    }
}
