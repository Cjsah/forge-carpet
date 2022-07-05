package net.cjsah.mod.carpet.fakes;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;

import java.util.Map;

public interface MobEntityInterface
{
    GoalSelector getAI(boolean target);

    Map<String, Goal> getTemporaryTasks();

    void setPersistence(boolean what);
}
