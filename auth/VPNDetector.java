package auth;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class VPNDetector {
    public static boolean isCloudFlareVPN() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostName = inetAddress.getHostName();
            return hostName.contains("CloudFlare Inc.");
        } catch (UnknownHostException e) {
            return false;
        }
    }
	
	public static boolean isAvastVPN() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            String interfaceName = networkInterface.getDisplayName();

            if (interfaceName.equals("Avast")) {
                return true;
            }
        }
		return false;
    }
}