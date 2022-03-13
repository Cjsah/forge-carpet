package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntity_creativeNoClipMixin {
    @Redirect(method = "pushEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"
    ))
    private PushReaction getPistonBehaviourOfNoClipPlayers(Entity entity) {
        if (CarpetSettings.creativeNoClip && entity instanceof Player && (((Player) entity).isCreative()) && ((Player) entity).getAbilities().flying)
            return PushReaction.IGNORE;
        return entity.getPistonPushReaction();
    }
}
