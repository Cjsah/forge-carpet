package net.cjsah.mod.carpet.fakes;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;

import java.util.List;
import java.util.Map;

public interface ThreadedAnvilChunkStorageInterface
{
    Map<String, Integer> regenerateChunkRegion(List<ChunkPos> requestedChunks);

    void relightChunk(ChunkPos pos);

    void releaseRelightTicket(ChunkPos pos);

    Iterable<ChunkHolder> getChunksCM();
}
