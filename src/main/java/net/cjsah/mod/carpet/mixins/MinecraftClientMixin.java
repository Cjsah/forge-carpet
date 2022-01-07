package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetServer;
import net.cjsah.mod.carpet.helpers.TickSpeed;
import net.cjsah.mod.carpet.network.CarpetClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin
{
    @Shadow public ClientLevel world;
    
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onCloseGame(Screen screen, CallbackInfo ci)
    {
        CarpetClient.disconnect();
    }
    
    @Inject(at = @At("HEAD"), method = "tick")
    private void onClientTick(CallbackInfo info) {
        if (this.world != null) {
            if (CarpetServer.minecraft_server == null)
                TickSpeed.tick();
            if (!TickSpeed.process_entities)
                CarpetClient.shapes.renewShapes();
        }
    }
}
