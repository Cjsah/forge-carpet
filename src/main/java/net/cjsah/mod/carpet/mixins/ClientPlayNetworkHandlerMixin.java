package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.network.CarpetClient;
import net.cjsah.mod.carpet.network.ClientNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow private Minecraft client;

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CarpetClient.CARPET_CHANNEL.equals(packet.getIdentifier())) {
            ClientNetworkHandler.handleData(packet.getData(), client.player);
            ci.cancel();
        }
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onGameJoined(ClientboundLoginPacket packet, CallbackInfo info) {
        CarpetClient.gameJoined(client.player);
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onCMDisconnected(Component reason, CallbackInfo ci) {
        CarpetClient.disconnect();
    }

}
