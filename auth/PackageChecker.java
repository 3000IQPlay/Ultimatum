package auth;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageChecker {
    public static void checkPackage() {
        try (JarFile jarFile = new JarFile(JarChecker.class.getProtectionDomain().getCodeSource().getLocation().getPath())) {
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

            if (hasPackage) {
                //System.out.println("This JAR file contains package " + packageName);
            } else {
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
				
                //System.out.println("This JAR file does not contain package " + packageName);
            }
        } catch (IOException e) {
            //System.out.println("Error checking JAR file: " + e.getMessage());
        }
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