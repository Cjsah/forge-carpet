package net.cjsah.mod.carpet.mixins;

import java.util.Set;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.cjsah.mod.carpet.fakes.ChunkTicketManagerInterface;

@Mixin(DistanceManager.class)
public abstract class ChunkTicketManager_scarpetChunkCreationMixin implements ChunkTicketManagerInterface {
    @Shadow
    @Final
    private Set<ChunkHolder> chunkHolders;

    @Override
    public void replaceHolder(final ChunkHolder oldHolder, final ChunkHolder newHolder) {
        this.chunkHolders.remove(oldHolder);
        this.chunkHolders.add(newHolder);
    }
}
