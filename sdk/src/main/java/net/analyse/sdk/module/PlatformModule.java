package net.analyse.sdk.module;

import net.analyse.sdk.Analyse;
import net.analyse.sdk.platform.Platform;

public abstract class PlatformModule {
    public Platform getPlatform() {
        return Analyse.get();
    }

    public abstract void onEnable();

    public void disable(String reason) {
//        getPlatform().getLogger().warning("Disabling module " + getClass().getSimpleName() + " because: " + reason);
//        getPlatform().getModuleLoader().disableModule(this);
    }
}
