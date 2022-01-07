package net.cjsah.mod.carpet.mixins;

import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DynamicGraphMinFixedPoint.class)
public interface LevelPropagator_resetChunkInterface
{
    @Invoker("updateLevel")
    void cmInvokeUpdateLevel(long sourceId, long id, int level, boolean decrease);

    @Invoker("getPropagatedLevel")
    int cmCallGetPropagatedLevel(long sourceId, long targetId, int level);
}
