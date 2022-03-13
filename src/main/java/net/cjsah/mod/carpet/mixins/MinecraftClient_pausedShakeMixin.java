package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.MinecraftClientInferface;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public class MinecraftClient_pausedShakeMixin implements MinecraftClientInferface {
    @Shadow private float pausedTickDelta;

    @Override
    public float getPausedTickDelta() {
        return pausedTickDelta;
    }
}
