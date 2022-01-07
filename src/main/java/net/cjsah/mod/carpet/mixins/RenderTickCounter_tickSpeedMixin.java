package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.helpers.TickSpeed;
import net.minecraft.client.Timer;
import net.cjsah.mod.carpet.CarpetSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Timer.class)
public class RenderTickCounter_tickSpeedMixin {
    @Redirect(method = "beginRenderTick", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/RenderTickCounter;tickTime:F"
    ))
    private float adjustTickSpeed(Timer counter) {
        if (CarpetSettings.smoothClientAnimations && TickSpeed.process_entities)
        {
            return Math.max(50.0f, TickSpeed.mspt);
        }
        return 50f;
    }
}