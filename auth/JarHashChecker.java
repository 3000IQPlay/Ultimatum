package auth;

import auth.AESEncryptor;
import auth.WebhookInformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarFile;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class JarHashChecker {

    public static void checkHash() throws NoSuchAlgorithmException, IOException {
        // Get the current JAR file's path
        String jarFilePath = JarHashChecker.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        // Get the link to the expected encrypted hash
        String hashLink = "https://pastebin.com/123"; // Replace with secure storage
		String expectedEncryptedHash = null;

        // Download the expected encrypted hash from the link
		if (hashLink != null && hashLink.equals("https://pastebin.com/123")) {
			URL hashURL = new URL(hashLink);
     		HttpURLConnection connection = (HttpURLConnection) hashURL.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			expectedEncryptedHash = reader.readLine();
		} else {
			// System.out.println("DEV MODE: String, containing the URL to the hash, doesn't match");
		
			// Informs the developers about a suspicious activity
			WebhookInformer.sendFlag("- Different JarHash URL has been detected -> " + hashLink);
			
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
				
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
		}

        // Calculate the actual hash of the JAR file
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = readFileBytes(jarFilePath);
        byte[] hash = sha256Digest.digest(fileBytes);

        // Convert the hash to a string
        String actualHash = new String(hash);
		
		// Encryp actual hash so it's harder to identify what it is for
		String key = "24-char-key";
		String actualEncryptedHash = AESEncryptor.encrypt(actualHash, key);

        // Check if the actual hash matches the expected hash
		if (expectedEncryptedHash != null) {
			if (actualEncryptedHash.equals(expectedEncryptedHash)) {
				// System.out.println("DEV MODE: JAR file is intact. Hash matches expected value: " + actualEncryptedHash);
			} else {
				// System.out.println("DEV MODE: JAR file has been modified (Hashes don't match). Expected hash: " + expectedEncryptedHash + ", Actual hash: " + actualEncryptedHash);
				
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag("- Different Jar Hash has been detected (Encrypted) -> Expected: " + expectedEncryptedHash + " Actual: " + actualEncryptedHash);
		
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
				
				/*Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);*/
			}
		} else {
			// System.out.println("DEV MODE: Hash is null");
			
			// Informs the developers about a suspicious activity
			WebhookInformer.sendFlag("- Expected Jar Hash is null.");
		
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
		}
    }

    private static byte[] readFileBytes(String filePath) throws IOException {
        try (JarFile jarFile = new JarFile(filePath)) {
            return jarFile.getInputStream(jarFile.getManifest()).readAllBytes();
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

