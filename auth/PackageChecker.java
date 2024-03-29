package auth;

import auth.WebhookInformer;

import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageChecker {
    public static void checkPackage() {
        try (JarFile jarFile = new JarFile(PackageChecker.class.getProtectionDomain().getCodeSource().getLocation().getPath())) {
            // Enumerate the JAR entries
            Enumeration<JarEntry> entries = jarFile.entries();

            // Check for the specified package
            String packageName = "me.example.com";
            boolean hasPackage = false;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packageName + "/")) {
                    hasPackage = true;
                    break;
                }
            }
			
			if (packageName != null || packageName.equals("me.example.com")) {
				// Empty
			} else {
				// System.out.println("DEV MODE: Different Package Name has been detected -> " + packageName);
				
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag("- Different Package Name has been detected -> " + packageName);
			
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
			}

            if (hasPackage) {
                // System.out.println("DEV MODE: This JAR file contains package " + packageName);
            } else {
				System.out.println("DEV MODE: This JAR file does not contain package " + packageName);
				
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag("- Package " + packageName + " has been removed/changed");
			
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
            }
        } catch (IOException e) {
            //System.out.println("Error checking JAR file: " + e.getMessage());
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