package net.cjsah.mod.carpet.patches;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;

public class NetworkManagerFake extends Connection {
    public NetworkManagerFake(PacketFlow p) {
        super(p);
    }

    @Override
    public void setReadOnly() {
    }

    @Override
    public void handleDisconnection() {
    }
}