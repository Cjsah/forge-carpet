package net.cjsah.mod.carpet.mixins;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.cjsah.mod.carpet.logging.logHelpers.PacketCounter;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class Connection_packetCounterMixin {
    // Add to the packet counter whenever a packet is received.
    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void packetInCount(ChannelHandlerContext channelHandlerContext_1, Packet<?> packet_1, CallbackInfo ci) {
        PacketCounter.totalIn++;
    }
    
    // Add to the packet counter whenever a packet is sent.
    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void packetOutCount(Packet<?> packet_1,
            GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1, CallbackInfo ci) {
        PacketCounter.totalOut++;
    }
}
