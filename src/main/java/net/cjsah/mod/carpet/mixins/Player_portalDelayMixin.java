package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class Player_portalDelayMixin
{
    @Final @Shadow public Abilities abilities;

    @Inject(method = "getPortalWaitTime()I", at = @At("HEAD"), cancellable = true)
    private void onMaxNetherPortalTime(CallbackInfoReturnable<Integer> cir) {
        if(CarpetSettings.portalCreativeDelay != 1 && this.abilities.invulnerable) cir.setReturnValue(CarpetSettings.portalCreativeDelay);
        else if(CarpetSettings.portalSurvivalDelay != 80 && !this.abilities.invulnerable) cir.setReturnValue(CarpetSettings.portalSurvivalDelay);
    }
}
