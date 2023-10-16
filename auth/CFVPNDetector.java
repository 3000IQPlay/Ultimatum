package auth;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CFVPNDetector {
    public boolean isVPN() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostName = inetAddress.getHostName();
            return hostName.contains("CloudFlare Inc.");
        } catch (UnknownHostException e) {
            return null;
        }
    }
}