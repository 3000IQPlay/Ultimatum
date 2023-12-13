package auth;

import auth.MacUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class AntiVM {
	public static void checkForVM() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nux") || osName.contains("nix") || osName.contains("mac")) {
            String cpuInfo = executeCommand("cat /proc/cpuinfo");
            if (cpuInfo.contains("hypervisor")) {
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag();
			
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
			
				/*Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);*/
                // System.out.println("Running on a Virtual Machine");
            } else {
                // System.out.println("Not running on a Virtual Machine");
            }
        } else if (osName.contains("win")) {
            String systemInfo = executeCommand("systeminfo");
            if (systemInfo.contains("Virtual Machine")) {
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag();
			
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
			
				/*Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);*/
                // System.out.println("Running on a Virtual Machine");
            } else {
                // System.out.println("Not running on a Virtual Machine");
            }
        } else {
            // System.out.println("Cannot determine the operating system");
        }
    }

    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
	
    public static boolean isRunningOnVM() {
        boolean isVM = false;

        String vendor = System.getProperty("java.vendor");
        String name = System.getProperty("java.vm.name");
        String version = System.getProperty("java.vm.version");
        String classPath = System.getProperty("java.class.path");

        if (vendor != null && vendor.toLowerCase().contains("vmware")) {
            isVM = true;
        } else if (name != null && name.toLowerCase().contains("virtualbox")) {
            isVM = true;
        } else if (version != null && version.toLowerCase().contains("virtual")) {
            isVM = true;
        } else if (classPath != null && classPath.toLowerCase().contains("android")) {
            isVM = true;
        } else if (classPath != null && classPath.toLowerCase().contains("sandbox")) {
            isVM = true;
        }

        File[] filesToCheck = {new File("C:\\WINDOWS\\system32\\drivers\\vmmouse.sys"),
                new File("/usr/share/virtualbox")};
        for (File file : filesToCheck) {
            if (file.exists()) {
                isVM = true;
                break;
            }
        }

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                Process process = Runtime.getRuntime().exec("reg query HKLM\\HARDWARE\\ACPI\\DSDT\\VBOX__");
                process.waitFor();
                if (process.exitValue() == 0) {
                    isVM = true;
                }
            } catch (IOException | InterruptedException e) {
            }
        }

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long availableMemory = maxMemory - totalMemory + freeMemory;
        if (availableProcessors <= 2 || availableMemory <= 1024 * 1024 * 512) {
            isVM = true;
        }

        return isVM;
    }
	
	public static boolean isMacVM() {
        try {
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();
            if (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                return MacUtil.isVMMac(element.getHardwareAddress());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	// Bootstrap method to generate the "MethodHandle" for "System.exit()"
    public static CallSite bootstrap() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?> klass = Class.forName("java.lang.System");
        MethodHandle methodHandle = MethodHandles.lookup().findStaticMethod(klass, "exit", int.class);
        return new ConstantCallSite(methodHandle);
    }

    // Link the bootstrap method to the CallSite
    public static CallSite generateExitCallSite() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CallSite callSite = new ConstantCallSite(bootstrap());
        return callSite;
    }
}
