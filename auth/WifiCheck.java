package auth;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
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
            return address.isReachable(1000);  // Returns TRUE is reachable
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
        } catch (SocketException | NoSuchMethodException | IllegalAccessException e) {
            // System.out.println("Error getting network interfaces: " + e.getMessage());
        }
    }

    public static void checkWifiConnectionWindows() throws SocketException, NoSuchMethodException, IllegalAccessException {
        if (isWifiConnection("Wi-Fi")) {
            // System.out.println("DEV MODE: Connected to WiFi.");
        } else {
			// System.out.println("DEV MODE: Not Connected to WiFi.");
			
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
            // System.out.println("Not connected to WiFi.");
        }
    }

    public static void checkWifiConnectionUnix() throws SocketException, NoSuchMethodException, IllegalAccessException {
        if (isWifiConnection("wlan") || isWifiConnection("en")) {
            // System.out.println("DEV MODE: Connected to WiFi.");
        } else {
			// System.out.println("DEV MODE: Not Connected to WiFi.");
			
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
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

    // bootstrapExit method to generate the "MethodHandle" for "System.exit()"
    public static CallSite bootstrapExit() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class<?> klass = Class.forName("java.lang.System");
        MethodHandle methodHandle = MethodHandles.lookup().findStaticMethod(klass, "exit", int.class);
        return new ConstantCallSite(methodHandle);
    }

    // Link the bootstrapExit method to the CallSite
    public static CallSite generateExitCallSite() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CallSite callSite = new ConstantCallSite(bootstrapExit());
        return callSite;
    }
}
