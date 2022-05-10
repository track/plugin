package net.analyse.sdk;

public class AnalyseCore {

    private static AnalyseSDK sdk;

    public static void setCore(AnalyseSDK sdk) {
        AnalyseCore.sdk = sdk;
    }

    public static AnalyseSDK getSDK() {
        return sdk;
    }

    private static String requestHeader = "Analyse";

    public static void setRequestHeader(String requestHeader) {
        AnalyseCore.requestHeader = requestHeader;
    }

    public static String getRequestHeader() {
        return requestHeader;
    }
}
