package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.logging.LoggerRegistry;
import net.cjsah.mod.carpet.logging.logHelpers.PathfindingVisualizer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

@Mixin(PathNavigation.class)
public abstract class EntityNavigation_pathfindingMixin {

    @Shadow @Final protected Mob entity;


    @Shadow protected @Nullable abstract Path findPathTo(Set<BlockPos> set, int i, boolean bl, int j);

    @Redirect(method =  "findPathTo(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;findPathTo(Ljava/util/Set;IZI)Lnet/minecraft/entity/ai/pathing/Path;"
    ))
    private Path pathToBlock(PathNavigation entityNavigation, Set<BlockPos> set_1, int int_1, boolean boolean_1, int int_2) {
        if (!LoggerRegistry.__pathfinding)
            return findPathTo(set_1, int_1, boolean_1, int_2);
        long start = System.nanoTime();
        Path path = findPathTo(set_1, int_1, boolean_1, int_2);
        long finish = System.nanoTime();
        float duration = (1.0F*((finish - start)/1000))/1000;
        set_1.forEach(b -> PathfindingVisualizer.slowPath(entity, Vec3.atBottomCenterOf(b), duration, path != null)); // ground centered position
        return path;
    }

    @Redirect(method =  "findPathTo(Lnet/minecraft/entity/Entity;I)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;findPathTo(Ljava/util/Set;IZI)Lnet/minecraft/entity/ai/pathing/Path;"
    ))
    private Path pathToEntity(PathNavigation entityNavigation, Set<BlockPos> set_1, int int_1, boolean boolean_1, int int_2) {
        if (!LoggerRegistry.__pathfinding)
            return findPathTo(set_1, int_1, boolean_1, int_2);
        long start = System.nanoTime();
        Path path = findPathTo(set_1, int_1, boolean_1, int_2);
        long finish = System.nanoTime();
        float duration = (1.0F*((finish - start)/1000))/1000;
        set_1.forEach(b -> PathfindingVisualizer.slowPath(entity, Vec3.atBottomCenterOf(b), duration, path != null));
        return path;
    }
}
