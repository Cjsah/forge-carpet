package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.PlayerListHudInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class InGameHudMixin
{
    @Shadow
    @Final
    private Minecraft client;

    @Shadow @Final private PlayerTabOverlay playerListHud;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
    private boolean onDraw(Minecraft minecraftClient)
    {
        return this.client.isLocalServer() && !((PlayerListHudInterface) playerListHud).hasFooterOrHeader();
    }

}
