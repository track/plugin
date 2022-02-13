package net.analyse.plugin.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import net.analyse.plugin.AnalysePlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

public class LocationUtil {
    private static final JavaPlugin plugin = JavaPlugin.getPlugin(AnalysePlugin.class);

    public static String fromIp(String ip) {
        InputStream database = plugin.getResource("GeoLite2-Country.mmdb");
        try {
            DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

            return dbReader.country(InetAddress.getByName(ip)).getCountry().getName();
        } catch (IOException | GeoIp2Exception e) {
            return null;
        }
    }
}
