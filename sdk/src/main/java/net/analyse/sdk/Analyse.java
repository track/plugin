package net.analyse.sdk;

import net.analyse.sdk.platform.Platform;

/**
 * The Analyse class serves as the entry point for the Analyse SDK and provides methods to
 * initialise and access the platform instance. The SDK is designed to work with various server
 * platforms, such as Bukkit or Sponge, through the use of the Platform interface.
 */
public class Analyse {
    private static Platform platform;

    /**
     * Private constructor to prevent instantiation of this singleton class.
     */
    public Analyse() {
        throw new UnsupportedOperationException("This is a singleton class and cannot be instantiated");
    }

    /**
     * Initialises the Analyse SDK with the provided platform instance.
     *
     * @param platform The platform instance to initialise the SDK with
     */
    public static void init(Platform platform) {
        Analyse.platform = platform;
    }

    /**
     * Retrieves the currently initialised platform instance.
     *
     * @return The current platform instance, or null if the SDK has not been initialized
     */
    public static Platform get() {
        return platform;
    }
}
