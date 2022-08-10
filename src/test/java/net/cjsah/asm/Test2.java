package net.cjsah.asm;

import net.cjsah.mod.carpet.utils.RandomTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Skeleton;

public class Test2 {
    private final SkeletonHorse horse = null;

    public void tick() {
        ServerLevel serverlevel = (ServerLevel)this.horse.level;
        // Forge: Trigger the trap in a tick task to avoid crashes when mods add goals to skeleton horses
        // (MC-206338/Forge PR #7509)
        serverlevel.getServer().tell(new net.minecraft.server.TickTask(serverlevel.getServer().getTickCount(), () -> {
            if (!this.horse.isAlive()) return;
            DifficultyInstance difficultyinstance = serverlevel.getCurrentDifficultyAt(this.horse.blockPosition());
            this.horse.setTrap(false);
            this.horse.setTamed(true);
            this.horse.setAge(0);
            LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(serverlevel);
            lightningbolt.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ());
            lightningbolt.setVisualOnly(true);
            serverlevel.addFreshEntity(lightningbolt);
            Skeleton skeleton = this.createSkeleton(difficultyinstance, this.horse);
            skeleton.startRiding(this.horse);
            serverlevel.addFreshEntityWithPassengers(skeleton);

            for(int i = 0; i < 3; ++i) {
                AbstractHorse abstracthorse = this.createHorse(difficultyinstance);
                Skeleton skeleton1 = this.createSkeleton(difficultyinstance, abstracthorse);
                skeleton1.startRiding(abstracthorse);
                abstracthorse.push(RandomTools.nextGauBian(this.horse.getRandom()) * 0.5D, 0.0D, RandomTools.nextGauBian(this.horse.getRandom()) * 0.5D);
                serverlevel.addFreshEntityWithPassengers(abstracthorse);
            }
        }));
    }

    private Skeleton createSkeleton(DifficultyInstance pDifficulty, AbstractHorse pHorse) {
        return null;
    }
    private AbstractHorse createHorse(DifficultyInstance pDifficulty) {
        return null;
    }
}
