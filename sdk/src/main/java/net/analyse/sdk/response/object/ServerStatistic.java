package net.analyse.sdk.response.object;

public class ServerStatistic {
    private final String nickname;
    private final String placeholder;

    public ServerStatistic(String nickname, String placeholder) {
        this.nickname = nickname;
        this.placeholder = placeholder;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
