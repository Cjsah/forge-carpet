package net.cjsah.mod.carpet.mixin;

import net.cjsah.mod.carpet.patch.EntityPlayerMPFake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorld_fakePlayersMixin
{
    @Shadow public abstract MinecraftServer getServer();

    @Shadow boolean tickingEntities;

    @Shadow public abstract void removeEntity(Entity entityIn);

    @Shadow public abstract void updateAllPlayersSleepingFlag();

    @SuppressWarnings("UnnecessaryReturnStatement")
    @Inject(method = "removePlayer(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At("HEAD"))
    private void removePlayer(ServerPlayerEntity player, CallbackInfo ci) {
        player.remove(false);
        if ( !(tickingEntities && player instanceof EntityPlayerMPFake) ) this.removeEntity(player);
        else {
            this.getServer().enqueue(new TickDelayedTask(getServer().getTickCounter(), () ->
            {
                this.removeEntity(player);
                player.clearInvulnerableDimensionChange();
            }));
        }
        this.updateAllPlayersSleepingFlag();
        return;
    }


}
