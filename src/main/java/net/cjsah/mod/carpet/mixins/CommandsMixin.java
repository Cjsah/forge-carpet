package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Commands.class)
public abstract class CommandsMixin {

    @Inject(method = "performCommand", at = @At("HEAD"))
    private void onExecuteBegin(CommandSourceStack commandSource, String string, CallbackInfoReturnable<Integer> cir) {
        if (!CarpetSettings.fillUpdates) CarpetSettings.impendingFillSkipUpdates.set(true);
    }

    @Inject(method = "performCommand", at = @At("RETURN"))
    private void onExecuteEnd(CommandSourceStack commandSource, String string, CallbackInfoReturnable<Integer> cir) {
        CarpetSettings.impendingFillSkipUpdates.set(false);
    }

    @Redirect(method = "performCommand", at = @At(
                value = "INVOKE",
                target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z"
            ),
        require = 0
    )
    private boolean doesOutputCommandStackTrace(Logger logger) {
        if (CarpetSettings.superSecretSetting) return true;
        return logger.isDebugEnabled();
    }
}