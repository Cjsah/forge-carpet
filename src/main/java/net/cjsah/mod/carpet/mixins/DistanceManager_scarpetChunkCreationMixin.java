package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.ChunkTicketManagerInterface;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(DistanceManager.class)
public abstract class DistanceManager_scarpetChunkCreationMixin implements ChunkTicketManagerInterface {
    @Shadow
    @Final
    private Set<ChunkHolder> chunksToUpdateFutures;

    @Override
    public void replaceHolder(final ChunkHolder oldHolder, final ChunkHolder newHolder) {
        this.chunksToUpdateFutures.remove(oldHolder);
        this.chunksToUpdateFutures.add(newHolder);
    }
}
