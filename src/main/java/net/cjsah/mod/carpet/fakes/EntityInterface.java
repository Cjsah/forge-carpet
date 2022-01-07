package net.cjsah.mod.carpet.fakes;

import net.cjsah.mod.carpet.script.EntityEventsGroup;

public interface EntityInterface
{
    float getMainYaw(float partialTicks);

    EntityEventsGroup getEventContainer();

    boolean isPermanentVehicle();

    void setPermanentVehicle(boolean permanent);

    int getPortalTimer();

    void setPortalTimer(int amount);

    int getPublicNetherPortalCooldown();
    void setPublicNetherPortalCooldown(int what);

}
