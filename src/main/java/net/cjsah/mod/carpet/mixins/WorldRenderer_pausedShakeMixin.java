package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.MinecraftClientInferface;
import net.cjsah.mod.carpet.helpers.TickSpeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LevelRenderer.class, priority = 69420)
public class WorldRenderer_pausedShakeMixin
{
    @Shadow @Final private Minecraft client;

    float initial = -1234.0f;

    // require 0 is for optifine being a bitch as it usually is.
    @ModifyVariable(method = "render", argsOnly = true, require = 0, ordinal = 0, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"
    ))
    private float changeTickPhase(float previous)
    {
        initial = previous;
        if (!TickSpeed.process_entities)
            return ((MinecraftClientInferface)client).getPausedTickDelta();
        return previous;
    }

    // require 0 is for optifine being a bitch as it usually is.
    @ModifyVariable(method = "render", argsOnly = true, require = 0, ordinal = 0 ,at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V",
            shift = At.Shift.BEFORE
    ))
    private float changeTickPhaseBack(float previous)
    {
        // this may not set with optifine bitch
        return initial==-1234.0f?previous:initial;
    }

}
