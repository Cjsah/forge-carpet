package net.cjsah.mod.mixit.mixin;

import net.cjsah.mod.mixit.patch.EntityPlayerMPFake;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderPearlEntity.class)
public abstract class ThrownEnderpearlEntity_fakePlayersMixin extends ProjectileItemEntity
{
    public ThrownEnderpearlEntity_fakePlayersMixin(EntityType<? extends ProjectileItemEntity> entityType_1, World world_1)
    {
        super(entityType_1, world_1);
    }

    @Redirect(method =  "onImpact", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetworkManager;isChannelOpen()Z"
    ))
    private boolean isConnectionGood(NetworkManager networkManager)
    {
        return networkManager.isChannelOpen() || getShooter() instanceof EntityPlayerMPFake;
    }
}
