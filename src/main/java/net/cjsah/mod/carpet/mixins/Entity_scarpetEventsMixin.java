package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.EntityInterface;
import net.cjsah.mod.carpet.script.EntityEventsGroup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.cjsah.mod.carpet.script.CarpetEventServer.Event.PLAYER_CHANGES_DIMENSION;

@Mixin(Entity.class)
public abstract class Entity_scarpetEventsMixin implements EntityInterface {
    @Shadow public Level level;
    @Shadow protected int portalTime;
    @Shadow private int portalCooldown;
    @Shadow private Vec3 position, deltaMovement;
    @Shadow public abstract boolean isRemoved();

    private boolean permanentVehicle;

    private final EntityEventsGroup events = new EntityEventsGroup((Entity) (Object)this);

    private Vec3 pos1, motion;

    @Override
    public EntityEventsGroup getEventContainer() {
        return events;
    }

    @Override
    public boolean isPermanentVehicle() {
        return permanentVehicle;
    }

    @Override
    public void setPermanentVehicle(boolean permanent) {
        permanentVehicle = permanent;
    }

    @Override
    public int getPublicNetherPortalCooldown() {
        return portalCooldown;
    }

    @Override
    public void setPublicNetherPortalCooldown(int what) {
        portalCooldown = what;
    }

    @Override
    public int getPortalTimer() {
        return portalTime;
    }

    @Override
    public void setPortalTimer(int amount) {
        portalTime = amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickCall(CallbackInfo ci) {
        events.onEvent(EntityEventsGroup.Event.ON_TICK);
    }


    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(CallbackInfo ci) {
        if (!isRemoved()) events.onEvent(EntityEventsGroup.Event.ON_REMOVED);  // ! isRemoved()
    }


    @Inject(method = "setPosRaw", at = @At("HEAD"))
    private void firstPos(CallbackInfo ci) {
        pos1 = this.position;
        motion = this.deltaMovement;
    }

    @Inject(method = "setPosRaw", at = @At("TAIL"))
    private void secondPos(CallbackInfo ci) {
        if(pos1!=this.position)
            events.onEvent(EntityEventsGroup.Event.ON_MOVE, motion, pos1, this.position);
    }

    private Vec3 previousLocation;
    private ResourceKey<Level> previousDimension;

    @Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD"))
    private void logPreviousCoordinates(ServerLevel serverWorld, CallbackInfoReturnable<Entity> cir) {
        if ((Object)this instanceof ServerPlayer player) {
            previousLocation = position;
            previousDimension = level.dimension();  //dimension type
        }
    }

    @Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"))
    private void atChangeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        if ((Object)this instanceof ServerPlayer player && PLAYER_CHANGES_DIMENSION.isNeeded()) {
            Vec3 to = null;
            if (!player.wonGame || previousDimension != Level.END || destination.dimension() != Level.OVERWORLD) { // end ow
                to = position;
            }
            PLAYER_CHANGES_DIMENSION.onDimensionChange(player, previousLocation, to, previousDimension, destination.dimension());


        }
    }

}
