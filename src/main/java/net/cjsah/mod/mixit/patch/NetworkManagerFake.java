package net.cjsah.mod.mixit.patch;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;

public class NetworkManagerFake extends NetworkManager
{
    public NetworkManagerFake(PacketDirection p)
    {
        super(p);
    }

    @Override
    public void disableAutoRead()
    {
    }

    @Override
    public void handleDisconnection()
    {
    }
}