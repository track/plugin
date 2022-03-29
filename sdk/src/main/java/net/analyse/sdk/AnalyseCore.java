package net.analyse.sdk;

public class AnalyseCore {

    private static AnalyseSDK sdk;

    public static void setCore(AnalyseSDK sdk) {
        AnalyseCore.sdk = sdk;
    }

    public static AnalyseSDK getSDK() {
        return sdk;
    }
}
