package net.cjsah.mod.carpet.fakes;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;

import java.util.List;

public interface SimpleEntityLookupInterface<T extends EntityAccess>
{
    List<T> getChunkEntities(ChunkPos chpos);
}
