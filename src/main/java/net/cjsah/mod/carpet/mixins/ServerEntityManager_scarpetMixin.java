package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.script.CarpetEventServer;
import net.cjsah.mod.carpet.script.CarpetScriptServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class ServerEntityManager_scarpetMixin {
    @Inject(method = "addEntity(Lnet/minecraft/world/entity/EntityLike;Z)Z", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityTrackingStatus;shouldTick()Z"
    ))
    private void handleAddedEntity(EntityAccess entityLike, boolean existing, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)entityLike;
        CarpetEventServer.Event event = CarpetEventServer.Event.ENTITY_HANDLER.get(entity.getType());
        if (event != null) {
            if (event.isNeeded()) {
                event.onEntityAction(entity, !existing);
            }
        }
        else {
            CarpetScriptServer.LOG.error("Failed to handle entity type " + entity.getType().getDescriptionId());
        }

        event = CarpetEventServer.Event.ENTITY_LOAD.get(entity.getType());
        if (event != null) {
            if (event.isNeeded()) {
                event.onEntityAction(entity, true);
            }
        }
        else {
            CarpetScriptServer.LOG.error("Failed to handle entity type " + entity.getType().getDescriptionId());
        }

    }

}
