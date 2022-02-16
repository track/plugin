package net.analyse.plugin.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import net.analyse.plugin.AnalysePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

public class LocationUtil {

    private final @NotNull AnalysePlugin plugin;

    public LocationUtil(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    public String fromIp(final @NotNull String ip) {
        final InputStream database = plugin.getResource("GeoLite2-Country.mmdb");

        try {
            return new DatabaseReader.Builder(database).build()
                    .country(InetAddress.getByName(ip))
                    .getCountry()
                    .getIsoCode();
        } catch (IOException | GeoIp2Exception e) {
            return null;
        }
    }
}
