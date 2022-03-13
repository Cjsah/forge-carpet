package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.MinecraftServerInterface;
import net.cjsah.mod.carpet.helpers.TickSpeed;
import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServer_scarpetMixin extends ReentrantBlockableEventLoop<TickTask> implements MinecraftServerInterface {
    public MinecraftServer_scarpetMixin(String string_1) {
        super(string_1);
    }

    @Shadow protected abstract void tick(BooleanSupplier booleanSupplier_1);

    @Shadow private long timeReference;

    @Shadow private long lastTimeReference;

    @Shadow public abstract boolean pollTask();

    @Shadow @Final protected LevelStorageSource.LevelStorageAccess session;

    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> worlds;

    @Shadow private ServerResources serverResourceManager;

    @Override
    public void forceTick(BooleanSupplier isAhead) {
        timeReference = lastTimeReference = Util.getMillis();
        tick(isAhead);
        while(pollTask()) {Thread.yield();}
    }

    @Override
    public LevelStorageSource.LevelStorageAccess getCMSession() {
        return session;
    }

    @Override
    public ServerResources getResourceManager() {
        return serverResourceManager;
    }

    @Override
    public Map<ResourceKey<Level>, ServerLevel> getCMWorlds() {
        return worlds;
    }

    @Inject(method = "tick", at = @At(
            value = "CONSTANT",
            args = "stringValue=tallying"
    ))
    public void tickTasks(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        if (!TickSpeed.process_entities)
            return;
        CarpetEventServer.Event.TICK.onTick();
        CarpetEventServer.Event.NETHER_TICK.onTick();
        CarpetEventServer.Event.ENDER_TICK.onTick();
    }


}
