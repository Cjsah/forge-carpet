package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.ChunkGeneratorInterface;
import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenerator_scarpetMixin implements ChunkGeneratorInterface
{

    @Shadow protected abstract List<StructurePlacement> getPlacementsForFeature(Holder<ConfiguredStructureFeature<?, ?>> holder);

    @Shadow public abstract void ensureStructuresGenerated();

    @Override
    public void initStrongholds()
    {
        ensureStructuresGenerated();
    }

    @Override
    public List<StructurePlacement> getPlacementsForFeatureCM(ConfiguredStructureFeature<?, ?> structure) {
        return getPlacementsForFeature(Holder.direct(structure));
    }
}
