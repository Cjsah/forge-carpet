package net.cjsah.mod.carpet.utils;

import net.cjsah.mod.carpet.fakes.ChunkTicketManagerInterface;
import net.cjsah.mod.carpet.fakes.ServerChunkManagerInterface;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;

public class SpawnChunks {
    public static void changeSpawnChunks(ServerChunkCache chunkManager, ChunkPos pos, int size) {
        DistanceManager ticketManager = ((ServerChunkManagerInterface)chunkManager).getCMTicketManager();
        ((ChunkTicketManagerInterface)ticketManager).changeSpawnChunks(pos, size);
    }
}
