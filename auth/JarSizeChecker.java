package auth;

import auth.AESEncryptor;
import auth.WebhookInformer;

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
				String encryptedJarSize = reader.readLine();
				
				int targetEncryptedJarSize = Integer.parseInt(encryptedJarSize);
			} else {
				// System.out.println("DEV MODE: Size links do not match -> " + sizeLink);
				
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag("- Different JarSize URL has been detected -> " + sizeLink);
		
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
			
				/*Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);*/
			}

			// Get the size of the running jar file
			URL currentJar = JarSizeChecker.class.getProtectionDomain().getCodeSource().getLocation();
			long currentJarSizeInBytes = currentJar.openConnection().getContentLength();
			int currentJarSizeInKilobytes = (int) (currentJarSizeInBytes / 1024);
			
			// Encrypt Jar Size for better security
			String KEY = "24-char-string";
			String currentEncryptedJarSize = AESEncryptor.encrypt(currentJarSizeInKilobytes.toString(), KEY);

			// Check if Size is null and after Compare the sizes
			if (targetJarSize != null) {
				if (targetEncryptedJarSize != currentEncryptedJarSize) {
					// System.out.println("DEV MODE: JAR size mismatch (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
				
					// Informs the developers about a suspicious activity
					WebhookInformer.sendFlag("- Different Jar Size has been detected (Encrypted) -> Expected: " + targetEncryptedJarSize + " Actual: " + currentEncryptedJarSize);
		
					// Create a CallSite
					CallSite callSite = generateExitCallSite();

					// Invoke the "exit()" method using the CallSite
					callSite.invokeInt(0);
			
					/*Class<?> systemClass = Class.forName("java.lang.System");
					Method method = systemClass.getDeclaredMethod("exit", int.class);
					method.invoke(null, 0);*/
				} else {
					// System.out.println("DEV MODE: Current jar is equal to the target jar (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
				}
			} else {
				// System.out.println("DEV MODE: Target Jar Size is null.");
				
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag("- Target Jar Size is null -> " + currentJarSizeInKilobytes.toString());
		
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
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
