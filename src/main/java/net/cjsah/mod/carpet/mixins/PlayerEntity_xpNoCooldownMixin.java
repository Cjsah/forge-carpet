package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public abstract class PlayerEntity_xpNoCooldownMixin {

    @Shadow
    protected abstract void collideWithEntity(Entity entity);

    @Redirect(method = "tickMovement",at = @At(value = "INVOKE", target = "java/util/List.add(Ljava/lang/Object;)Z"))
    public boolean processXpOrbCollisions(List<Entity> instance, Object e) {
        Entity entity = (Entity) e;
        if (CarpetSettings.xpNoCooldown) {
            this.collideWithEntity(entity);
            return true;
        }
        return instance.add(entity);
    }
}
