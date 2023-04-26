package net.analyse.sdk;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static net.analyse.sdk.util.MapperUtil.sanitiseDomainAddress;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MapperUtilTest {

    @Test
    public void aValidDomainWillPass() {
        String domain = "blockga.me";
        assertNotNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));
    }

    @Test
    public void aValidSubDomainWillPass() {
        String domain = "play.blockga.me";
        assertNotNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));
    }

    @Test
    public void anIpAddressV4WillFail() {
        String domain = "123.123.123.123";
        assertNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));
    }

    @Test
    public void anIpAddressV6WillFail() {
        String domain = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        assertNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));
    }

    @Test
    public void anInvalidDomainWillFail() {
        String domainWithDashedIpPattern = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?:\\\\-(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){2}\\\\..";

        String domain = "ns1000000.ip-123-123-123.us";

        System.out.println(domain.matches(domainWithDashedIpPattern));

        assertNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));

        domain = "ns1000000.ip-10-100-10.net";
        assertNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));

        domain = "ns1000000.ip-10-100-1.eu";
        assertNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));

        domain = "100-1-10-100.ip.linodeusercontent.com";
        assertNull(sanitiseDomainAddress(new InetSocketAddress(domain, 25565)));
    }
}
