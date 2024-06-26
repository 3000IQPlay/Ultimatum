package auth;

import auth.WebhookInformer;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.*;

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
	
	public static boolean globalVPN() {
		// Check for VPN traffic using network traffic patterns
        Pattern tunPattern = Pattern.compile("^tun\\d+$");
        Pattern pppPattern = Pattern.compile("^ppp\\d+$");

        // Get network interface information
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface interface1 = interfaces.nextElement();
                List<InetAddress> addresses = (List<InetAddress>) interface1.getInetAddresses();
                for (InetAddress address : addresses) {
                    if (address.isMulticastAddress() && !address.isLoopbackAddress()) {
                        String interfaceName = interface1.getName();
                        String interfaceType = interface1.getDisplayName();
                        String addressString = address.getHostAddress();

                        // Check for tun0 and ppp0 interfaces, which are often used for VPN connections
                        if (tunPattern.matcher(interfaceName).matches() || pppPattern.matcher(interfaceName).matches()) {
                            /*System.out.println("Potential VPN connection detected:");
                            System.out.println("Interface name: " + interfaceName);
                            System.out.println("Interface type: " + interfaceType);
                            System.out.println("Address: " + addressString);*/
                            return true;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            //System.out.println("Error checking network interfaces: " + e.getLocalizedMessage());
        }

        // If no VPN traffic is detected, print message
        //System.out.println("No VPN traffic detected");
		return false;
	}
	
	public static void isVPN() throws SocketException, NoSuchMethodException, IllegalAccessException {
		if (isAvastVPN() || isCloudFlareVPN()) {
			// System.out.println("DEV MODE: VPN has been detected!");
			
			// Informs the developers about a suspicious activity
			WebhookInformer.sendFlag("- VPN has been detected.");
			
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
		}
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