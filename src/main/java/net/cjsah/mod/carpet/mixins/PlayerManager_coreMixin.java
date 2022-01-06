package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetServer;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManager_coreMixin
{
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnected(Connection connection, ServerPlayer player, CallbackInfo ci)
    {
        CarpetServer.onPlayerLoggedIn(player);
        CarpetEventServer.Event.PLAYER_CONNECTS.onPlayerEvent(player);
    }
}
