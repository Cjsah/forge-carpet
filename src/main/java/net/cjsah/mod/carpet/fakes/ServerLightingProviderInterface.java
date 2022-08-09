package net.cjsah.mod.carpet.fakes;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.CompletableFuture;

public interface ServerLightingProviderInterface {
    void invokeUpdateChunkStatus(ChunkPos pos);

    void removeLightData(ChunkAccess chunk);

    CompletableFuture<Void> relight(ChunkAccess chunk);

    void resetLight(ChunkAccess chunk, ChunkPos pos);
}
