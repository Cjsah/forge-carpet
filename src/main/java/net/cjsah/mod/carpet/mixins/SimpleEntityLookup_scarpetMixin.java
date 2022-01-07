package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.SimpleEntityLookupInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;

@Mixin(LevelEntityGetterAdapter.class)
public class SimpleEntityLookup_scarpetMixin<T extends EntityAccess> implements SimpleEntityLookupInterface
{

    @Shadow @Final private EntitySectionStorage<T> cache;

    @Override
    public List<T> getChunkEntities(ChunkPos chpos) {
        return this.cache.getExistingSectionsInChunk(chpos.toLong()).flatMap(EntitySection::getEntities).collect(Collectors.toList());
    }
}


