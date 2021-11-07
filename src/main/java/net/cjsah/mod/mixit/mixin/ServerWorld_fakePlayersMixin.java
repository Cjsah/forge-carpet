package net.cjsah.mod.mixit.mixin;

import net.cjsah.mod.mixit.patch.EntityPlayerMPFake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorld_fakePlayersMixin
{
    @Shadow /*@Nonnull*/ public abstract MinecraftServer getServer();

    @Shadow boolean tickingEntities;

    @Redirect( method = "removePlayer*", at  = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/server/ServerWorld;removeEntity(Lnet/minecraft/entity/Entity;Z)V",
            args = {"log=true"}
    ))
    private void crashRemovePlayer(ServerWorld world, Entity entity, boolean keepData)
    {
        if ( !(tickingEntities && entity instanceof EntityPlayerMPFake) )
            world.removeEntity(entity);
        else
            getServer().enqueue(new TickDelayedTask(getServer().getTickCounter(), () ->
            {
                world.removeEntity(entity);
                ((ServerPlayerEntity)entity).clearInvulnerableDimensionChange();
            }));

    }


}
