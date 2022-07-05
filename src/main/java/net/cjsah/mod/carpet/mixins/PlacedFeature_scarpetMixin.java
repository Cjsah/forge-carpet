package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.PlacedFeatureInterface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlacedFeature.class)
public class PlacedFeature_scarpetMixin implements PlacedFeatureInterface {

}
