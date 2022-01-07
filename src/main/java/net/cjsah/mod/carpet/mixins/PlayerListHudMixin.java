package net.cjsah.mod.carpet.mixins;
import net.cjsah.mod.carpet.fakes.PlayerListHudInterface;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerListHudMixin implements PlayerListHudInterface
{
    @Shadow private Component footer;

    @Shadow private Component header;

    public boolean hasFooterOrHeader()
    {
        return footer != null || header != null;
    }
}