package net.analyse.sdk;

import net.analyse.sdk.platform.Platform;

public class Analyse {
    private static Platform platform;

    public Analyse() {
        throw new UnsupportedOperationException("This is a singleton class and cannot be instantiated");
    }

    public static void init(Platform platform) {
        Analyse.platform = platform;
    }

    public static Platform get() {
        return platform;
    }
}