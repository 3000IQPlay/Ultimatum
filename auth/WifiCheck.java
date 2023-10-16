package auth;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class WifiCheck {
    public static boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return address.isReachable(1000);
        } catch (UnknownHostException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
	
	public static void localNetworkCheck() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")){
                checkWifiConnectionWindows();
            } else if(os.contains("nix") || os.contains("nux") || os.contains("mac")){
                checkWifiConnectionUnix();
            } else {
                // System.out.println("Unsupported operating system.");
            }
        } catch (SocketException e) {
            // System.out.println("Error getting network interfaces: " + e.getMessage());
        }
    }

    public static void checkWifiConnectionWindows() throws SocketException {
        if (isWifiConnection("Wi-Fi")) {
            // System.out.println("Connected to WiFi.");
        } else {
			Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);
            // System.out.println("Not connected to WiFi.");
        }
    }

    public static void checkWifiConnectionUnix() throws SocketException {
        if (isWifiConnection("wlan") || isWifiConnection("en")) {
            // System.out.println("Connected to WiFi.");
        } else {
			Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);
            // System.out.println("Not connected to WiFi.");
        }
    }

    public static boolean isWifiConnection(String interfaceName) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) continue;
            if (iface.getDisplayName().contains(interfaceName)) {
                return true;
            }
        }
        return false;
    }
}
