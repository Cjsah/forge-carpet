package net.cjsah.mod.carpet.mixins;

import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PoiRecord.class)
public interface PointOfInterest_scarpetMixin {
    @Accessor("freeTickets")
    int getFreeTickets();

    @Invoker
    boolean callReserveTicket();
}
