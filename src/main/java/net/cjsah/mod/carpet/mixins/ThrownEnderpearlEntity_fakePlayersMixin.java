package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.patches.EntityPlayerMPFake;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlEntity_fakePlayersMixin extends ThrowableItemProjectile
{
    public ThrownEnderpearlEntity_fakePlayersMixin(EntityType<? extends ThrowableItemProjectile> entityType_1, Level world_1)
    {
        super(entityType_1, world_1);
    }

    @Redirect(method =  "onCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;isOpen()Z"
    ))
    private boolean isConnectionGood(Connection clientConnection)
    {
        return clientConnection.isConnected() || getOwner() instanceof EntityPlayerMPFake;
    }
}
