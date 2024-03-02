package auth;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JarSizeChecker {
    public static void checkSize() throws IOException {
	try {
		// Get the link to the expected size
		String sizeLink = "https://pastebin.com/123";
			
		// Read the expected size from URL
		if (sizeLink != null && sizeLink.equals("https://pastebin.com/123")) {
			URL url = new URL(sizeLink);
			URLConnection connection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String jarSizeString = reader.readLine();
			int targetJarSize = Integer.parseInt(jarSizeString);
		} else {
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
			System.out.println("DEV MODE: String, containing the URL to the size, doesn't match");
		}

        	// Get the size of the running jar file
        	URL currentJar = JarSizeChecker.class.getProtectionDomain().getCodeSource().getLocation();
        	long currentJarSizeInBytes = currentJar.openConnection().getContentLength();
        	int currentJarSizeInKilobytes = (int) (currentJarSizeInBytes / 1024);

        	// Compare the sizes
            	if (targetJarSize != currentJarSizeInKilobytes) {
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
                	System.out.println("DEV MODE: JAR size mismatch (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
            	} else {
                	// System.out.println("DEV MODE: Current jar is equal to the target jar (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
            	}

        	} catch (IOException e) {
            		// System.out.println("An error occurred: " + e.getMessage());
		}
    }
	
    // bootstrapExit method to generate the "MethodHandle" for "System.exit()"
    public static CallSite bootstrapExit() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
