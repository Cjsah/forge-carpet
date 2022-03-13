package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.BrainInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

@Mixin(Brain.class)
public class Brain_Mixin implements BrainInterface {

    @Shadow @Final private Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories;

    @Override
    public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMobMemories() {
        return memories;
    }
}
