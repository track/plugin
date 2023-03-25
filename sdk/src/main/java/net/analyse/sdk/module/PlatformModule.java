package net.analyse.sdk.module;

import net.analyse.sdk.Analyse;
import net.analyse.sdk.platform.Platform;

public abstract class PlatformModule {
    public abstract String getName();
    public abstract void onEnable();
    public abstract void onDisable();

    public Platform getPlatform() {
        return Analyse.get();
    }

    public String getRequiredPlugin() {
        return null;
    }
}
