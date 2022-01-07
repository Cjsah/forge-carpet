package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerLevel.class)
public class ServerWorld_spawnChunksMixin
{
    @ModifyConstant(method = "setSpawnPos", constant = @Constant(intValue = 11), expect = 2)
    private int pushLimit(int original)
    {
        return CarpetSettings.spawnChunksSize;
    }
}
