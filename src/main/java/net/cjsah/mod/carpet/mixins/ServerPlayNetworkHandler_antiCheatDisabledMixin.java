package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandler_antiCheatDisabledMixin {
    @Shadow private int floatingTicks;

    @Shadow private int vehicleFloatingTicks;

    @Shadow protected abstract boolean isHost();

    @Inject(method = "tick", at = @At("HEAD"))
    private void restrictFloatingBits(CallbackInfo ci) {
        if (CarpetSettings.antiCheatDisabled) {
            if (floatingTicks > 70) floatingTicks--;
            if (vehicleFloatingTicks > 70) vehicleFloatingTicks--;
        }

    }

    @Redirect(method = "onVehicleMove", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"
    ))
    private boolean isServerTrusting(ServerGamePacketListenerImpl serverPlayNetworkHandler) {
        return isHost() || CarpetSettings.antiCheatDisabled;
    }

    @Redirect(method = "onPlayerMove", require = 0, // don't crash with immersive portals,
             at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;isInTeleportationState()Z"))
    private boolean relaxMoveRestrictions(ServerPlayer serverPlayerEntity) {
        return CarpetSettings.antiCheatDisabled || serverPlayerEntity.isChangingDimension();
    }
}
