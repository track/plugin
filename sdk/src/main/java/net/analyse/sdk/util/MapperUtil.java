package net.analyse.sdk.util;

import java.net.InetSocketAddress;

public class MapperUtil {
    public static String mapVirtualDomainToPlayer(InetSocketAddress virtualDomain) {
        if (virtualDomain == null) return null;

        String hostName = virtualDomain.getHostString();

        // A fix for TCPShield
        if (hostName.contains("///")) {
            hostName = hostName.split("///")[0];
        }

        // A fix for SRV Records
        if (hostName.contains("._minecraft._tcp.")) {
            hostName = hostName.split("._minecraft._tcp.", 2)[1];
        }

        // If the domain ends in a dot, remove it (so it's a valid domain).
        if (hostName.endsWith(".")) {
            hostName = hostName.substring(0, hostName.length() - 1);
        }

        return hostName;
    }
}