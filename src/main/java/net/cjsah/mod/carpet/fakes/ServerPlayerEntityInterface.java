package net.cjsah.mod.carpet.fakes;

import net.cjsah.mod.carpet.helpers.EntityPlayerActionPack;

public interface ServerPlayerEntityInterface {
    EntityPlayerActionPack getActionPack();
    void invalidateEntityObjectReference();
    boolean isInvalidEntityObject();
    String getLanguage();
}
