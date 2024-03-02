package auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarFile;

public class JarHashChecker {

    public static void checkHash() throws NoSuchAlgorithmException, IOException {
        // Get the current JAR file's path
        String jarFilePath = JarHashChecker.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        // Get the link to the expected hash
        String hashLink = "https://pastebin.com/123"; // Replace with secure storage

        // Download the expected hash from the link
	if (hashLink != null && hashLink.equals("https://pastebin.com/123")) {
		URL hashURL = new URL(hashLink);
      		HttpURLConnection connection = (HttpURLConnection) hashURL.openConnection();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        	String expectedHash = reader.readLine();
	} else {
		// Informs the developers about a suspicious activity
		WebhookInformer.sendFlag();
			
		// Create a CallSite
		CallSite callSite = generateExitCallSite();

		// Invoke the "exit()" method using the CallSite
		callSite.invokeInt(0);
				
		/*Class<?> systemClass = Class.forName("java.lang.System");
		Method method = systemClass.getDeclaredMethod("exit", int.class);
		method.invoke(null, 0);*/
		// System.out.println("WARNING: JAR file has been modified. Expected hash: " + expectedHash + ", Actual hash: " + actualHash);
	}

        // Calculate the actual hash of the JAR file
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = readFileBytes(jarFilePath);
        byte[] hash = sha256Digest.digest(fileBytes);

        // Convert the hash to a string
        String actualHash = new String(hash);

        // Check if the actual hash matches the expected hash
	if (expectedHash != null) {
		if (actualHash.equals(expectedHash)) {
			// System.out.println("JAR file is intact. Hash matches expected value: " + actualHash);
		} else {
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
				
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
			// System.out.println("WARNING: JAR file has been modified. Expected hash: " + expectedHash + ", Actual hash: " + actualHash);
		}
	} else {
		// Create a CallSite
		CallSite callSite = generateExitCallSite();

		// Invoke the "exit()" method using the CallSite
		callSite.invokeInt(0);
			
		/*Class<?> systemClass = Class.forName("java.lang.System");
		Method method = systemClass.getDeclaredMethod("exit", int.class);
		method.invoke(null, 0);*/
		// System.out.println("ERROR: Hash is null);
	}
    }

    private static byte[] readFileBytes(String filePath) throws IOException {
        try (JarFile jarFile = new JarFile(filePath)) {
            return jarFile.getInputStream(jarFile.getManifest()).readAllBytes();
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

