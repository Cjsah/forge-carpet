package net.cjsah.mod.carpet.mixin;

import com.mojang.authlib.GameProfile;
import net.cjsah.mod.carpet.fake.ServerPlayerEntityInterface;
import net.cjsah.mod.carpet.player.EntityPlayerActionPack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityInterface
{
    @Final
    @Shadow
    private static Logger LOGGER;
    public EntityPlayerActionPack actionPack;
    public EntityPlayerActionPack getActionPack()
    {
        return actionPack;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onServerPlayerEntityContructor(
            MinecraftServer minecraftServer_1,
            ServerLevel serverWorld_1,
            GameProfile gameProfile_1,
            CallbackInfo ci)
    {
        this.actionPack = new EntityPlayerActionPack((ServerPlayer) (Object) this);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void onTick(CallbackInfo ci)
    {
        try
        {
            actionPack.onUpdate();
        }
        catch (StackOverflowError soe)
        {
            LOGGER.fatal("Caused stack overflow when performing player action", soe);
        }
        catch (Throwable exc)
        {
            LOGGER.fatal("Error executing player tasks ", exc);
        }
    }



}
