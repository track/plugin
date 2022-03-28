package net.analyse.sdk.request;

import net.analyse.sdk.AnalyseSDK;

public class AnalyseRequest {

    public final String toJson() {
        return AnalyseSDK.GSON.toJson(this);
    }

}
