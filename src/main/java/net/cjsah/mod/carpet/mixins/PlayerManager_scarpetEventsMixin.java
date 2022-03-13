package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetServer;
import net.cjsah.mod.carpet.fakes.ServerPlayerEntityInterface;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerManager_scarpetEventsMixin {
    @Inject(method = "respawnPlayer", at = @At("HEAD"))
    private void onRespawn(ServerPlayer player, boolean olive, CallbackInfoReturnable<ServerPlayer> cir) {
        CarpetEventServer.Event.PLAYER_RESPAWNS.onPlayerEvent(player);
    }

    @Inject(method = "respawnPlayer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"
            //target = "Lnet/minecraft/server/network/ServerPlayerEntity;method_34225()V"
    ))
    private void invalidatePreviousInstance(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        ((ServerPlayerEntityInterface)player).invalidateEntityObjectReference();
    }

    @Inject(method = "onDataPacksReloaded", at = @At("HEAD"))
    private void reloadCommands(CallbackInfo ci) {
        CarpetServer.scriptServer.reAddCommands();
    }
}
