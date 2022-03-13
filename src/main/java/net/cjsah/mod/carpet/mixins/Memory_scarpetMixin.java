package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.MemoryInterface;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExpirableValue.class)
public class Memory_scarpetMixin implements MemoryInterface {
    @Shadow private long expiry;

    @Override
    public long getScarpetExpiry() {
        return expiry;
    }
}
