package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetServer;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandler_coreMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onPlayerDisconnect(Component reason, CallbackInfo ci) {
        CarpetServer.onPlayerLoggedOut(this.player);
        if (CarpetEventServer.Event.PLAYER_DISCONNECTS.isNeeded()) CarpetEventServer.Event.PLAYER_DISCONNECTS.onPlayerMessage(player, reason.getContents());
    }
}
