package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbEntityMixin extends Entity {
    @Shadow
    private int pickingCount;

    @Shadow
    private int amount;

    public ExperienceOrbEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    protected abstract int repairPlayerGears(Player player, int amount);

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void addXP(Player player, CallbackInfo ci) {
        if (CarpetSettings.xpNoCooldown && !level.isClientSide) {
            player.takeXpDelay = 0;
            // reducing the count to 1 and leaving vanilla to deal with it
            while (this.pickingCount > 1) {
                int remainder = this.repairPlayerGears(player, this.amount);
                if (remainder > 0) {
                    player.giveExperiencePoints(remainder);
                }
                this.pickingCount--;
            }
        }
    }
}
