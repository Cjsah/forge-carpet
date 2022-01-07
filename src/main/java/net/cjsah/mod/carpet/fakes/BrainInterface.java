package net.cjsah.mod.carpet.fakes;

import java.util.Map;
import java.util.Optional;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public interface BrainInterface
{
    Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMobMemories();
}
