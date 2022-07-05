package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.MobEntityInterface;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(Mob.class)
public abstract class MobMixin implements MobEntityInterface
{
    @Shadow @Final protected GoalSelector targetSelector;
    @Shadow @Final protected GoalSelector goalSelector;
    @Shadow private boolean persistenceRequired;
    public final Map<String, Goal> temporaryTasks = new HashMap<>();

    @Override
    public GoalSelector getAI(boolean target)
    {
        return target?targetSelector:goalSelector;
    }

    @Override
    public Map<String, Goal> getTemporaryTasks()
    {
        return temporaryTasks;
    }

    @Override
    public void setPersistence(boolean what)
    {
        persistenceRequired = what;
    }
}
