package net.analyse.plugin.request;

import net.analyse.plugin.AnalysePlugin;

public class AnalyseRequest {

    public final String toJson() {
        return AnalysePlugin.GSON.toJson(this);
    }

}
