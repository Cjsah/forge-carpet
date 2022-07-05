package net.cjsah.mod.carpet.mixins;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.cjsah.mod.carpet.fakes.ChunkTicketManagerInterface;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.SortedArraySet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DistanceManager.class)
public abstract class DistanceManager_scarpetMixin implements ChunkTicketManagerInterface
{
    @Shadow @Final private Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets;

    @Override
    public Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>>  getTicketsByPosition()
    {
        return tickets;
    }

}
