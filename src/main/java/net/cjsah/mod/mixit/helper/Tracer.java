package net.cjsah.mod.mixit.helper;

import net.minecraft.entity.Entity;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class Tracer {
    public static RayTraceResult rayTrace(Entity source, float partialTicks, double reach, boolean fluids) {
        BlockRayTraceResult blockHit = rayTraceBlocks(source, partialTicks, reach, fluids);
        double maxSqDist = reach * reach;
        if (blockHit != null) {
            maxSqDist = blockHit.getHitVec().squareDistanceTo(source.getEyePosition(partialTicks));
        }
        EntityRayTraceResult entityHit = rayTraceEntities(source, partialTicks, reach, maxSqDist);
        return entityHit == null ? blockHit : entityHit;
    }

    public static BlockRayTraceResult rayTraceBlocks(Entity source, float partialTicks, double reach, boolean fluids)
    {
        Vector3d pos = source.getEyePosition(partialTicks);
        Vector3d rotation = source.getLook(partialTicks);
        Vector3d reachEnd = pos.add(rotation.x * reach, rotation.y * reach, rotation.z * reach);
        return source.world.rayTraceBlocks(new RayTraceContext(pos, reachEnd, RayTraceContext.BlockMode.OUTLINE, fluids ?
                RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, source));
    }

    public static EntityRayTraceResult rayTraceEntities(Entity source, float partialTicks, double reach, double maxSqDist)
    {
        Vector3d pos = source.getEyePosition(partialTicks);
        Vector3d reachVec = source.getLook(partialTicks).scale(reach);
        AxisAlignedBB box = source.getBoundingBox().expand(reachVec).grow(1);
        return rayTraceEntities(source, pos, pos.add(reachVec), box, e -> !e.isSpectator() && e.canBeCollidedWith(), maxSqDist);
    }

    public static EntityRayTraceResult rayTraceEntities(Entity source, Vector3d start, Vector3d end, AxisAlignedBB box, Predicate<Entity> predicate, double maxSqDistance)
    {
        World world = source.world;
        double targetDistance = maxSqDistance;
        Entity target = null;
        Vector3d targetHitPos = null;
        for (Entity current : world.getEntitiesInAABBexcluding(source, box, predicate))
        {
            AxisAlignedBB currentBox = current.getBoundingBox().grow(current.getCollisionBorderSize());
            Optional<Vector3d> currentHit = currentBox.rayTrace(start, end);
            if (currentBox.contains(start))
            {
                if (targetDistance >= 0)
                {
                    target = current;
                    targetHitPos = currentHit.orElse(start);
                    targetDistance = 0;
                }
            }
            else if (currentHit.isPresent())
            {
                Vector3d currentHitPos = currentHit.get();
                double currentDistance = start.squareDistanceTo(currentHitPos);
                if (currentDistance < targetDistance || targetDistance == 0)
                {
                    if (current.getLowestRidingEntity() == source.getLowestRidingEntity())
                    {
                        if (targetDistance == 0)
                        {
                            target = current;
                            targetHitPos = currentHitPos;
                        }
                    }
                    else
                    {
                        target = current;
                        targetHitPos = currentHitPos;
                        targetDistance = currentDistance;
                    }
                }
            }
        }
        if (target == null) return null;
        return new EntityRayTraceResult(target, targetHitPos);
    }
}
