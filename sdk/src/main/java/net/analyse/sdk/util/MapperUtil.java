package net.analyse.sdk.util;

import java.net.InetSocketAddress;

public class MapperUtil {
    public static String mapVirtualDomainToPlayer(InetSocketAddress virtualDomain) {
        if (virtualDomain == null) return null;

        String hostName = virtualDomain.getHostName();
        if (hostName.contains("._minecraft._tcp.")) {
            hostName = hostName.split("._minecraft._tcp.", 2)[1];
        }

        if (hostName.endsWith(".")) {
            hostName = hostName.substring(0, hostName.length() - 1);
        }

        return hostName;
    }
}