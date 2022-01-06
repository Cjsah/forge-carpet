package net.cjsah.mod.carpet.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

@Mixin(Explosion.class)
public interface ExplosionAccessor {

    @Accessor
    boolean isCreateFire();

    @Accessor
    Explosion.BlockInteraction getDestructionType();

    @Accessor
    Level getWorld();

    @Accessor
    Random getRandom();

    @Accessor
    double getX();

    @Accessor
    double getY();

    @Accessor
    double getZ();

    @Accessor
    float getPower();

    @Accessor
    Entity getEntity();

}
