package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LevelRenderer.class, priority = 69420)
public class WorldRenderer_fogOffMixin {
    @Redirect(method = "render", require = 0, expect = 0, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/SkyProperties;useThickFog(II)Z"
    ))
    private boolean isReallyThick(SkyProperties skyProperties, int x, int z) {
        if (CarpetSettings.fogOff) return false;
        return skyProperties.useThickFog(x, z);
    }

}
