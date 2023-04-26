package net.analyse.sdk.util;

import java.net.InetSocketAddress;

/**
 * Utility class to handle mapping and sanitising of domain addresses.
 */
public class MapperUtil {

    /**
     * Sanitises the given domain address by applying checks and fixes, such as removing SRV record data.
     *
     * @param virtualDomain The {@link InetSocketAddress} containing the domain address to sanitise.
     * @return The sanitised domain address, or null if the input is null.
     */
    public static String sanitiseDomainAddress(InetSocketAddress virtualDomain) {
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

        // Check if the host name is an IP address (IPv4 or IPv6)
        String ipv4Pattern = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String ipv6Pattern = "^(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}$";

        if (hostName.matches(ipv4Pattern) || hostName.matches(ipv6Pattern)) {
            return null;
        }

        String domainWithDashedIpPattern = ".*(?:\\d{1,3}\\-){3}\\d{1,3}.*";

        if (hostName.matches(domainWithDashedIpPattern)) {
            return null;
        }

        return hostName;
    }
}