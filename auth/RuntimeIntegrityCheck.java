package auth;

import auth.WebhookInformer;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.security.Signature;
import java.util.jar.Manifest;
import java.util.jar.JarFile;

public class RuntimeIntegrityCheck {
	public static void integrityCheck() throws Exception {
		// Get the JAR file path
		String jarFilePath = RuntimeIntegrityCheck.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        // Load the JAR file
        JarFile jarFile = new JarFile(jarFilePath);

        // Get the JAR file's manifest
        Manifest manifest = jarFile.getManifest();

        // Extract the JAR file's signature file path
        String signatureFilePath = getSignatureFilePath(manifest);

        // Verify the JAR file signature
        boolean isJarFileValid = verifyJarFileSignature(jarFilePath, signatureFilePath);

        if (isJarFileValid) {
            // System.out.println("JAR file is valid");
        } else {
			// System.out.println("DEV MODE: Jar file is not valid.");
			
			// Informs the developers about a suspicious activity
			WebhookInformer.sendFlag("- Jar file is not valid (Signature).");
			
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
            // System.out.println("JAR file is invalid");
			/*Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);*/
        }

        // Close the JAR file
        jarFile.close();
    }

    private static String getSignatureFilePath(Manifest manifest) {
        String signatureFile = manifest.getMainAttributes().getValue("SF");
        if (signatureFile == null) {
            throw new IllegalStateException("No signature file found in the JAR file manifest");
        }
        return signatureFile;
    }

    private static boolean verifyJarFileSignature(String jarFilePath, String signatureFilePath) throws Exception {
        // Read the JAR file contents
        File jarFile = new File(jarFilePath);
        byte[] jarFileBytes = Files.readAllBytes(jarFile.toPath());

        // Read the signature file contents
        //File signatureFile = new File(signatureFilePath);
        //byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());

        // Create a Signature object
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Verify the JAR file signature
        boolean isVerified = signature.verify(jarFileBytes);

        return isVerified;
    }
	
	// bootstrapExit method to generate the "MethodHandle" for "System.exit()"
    public static CallSite bootstrapExit() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class<?> klass = Class.forName("java.lang.System");
        MethodHandle methodHandle = MethodHandles.lookup().findStaticMethod(klass, "exit", int.class);
        return new ConstantCallSite(methodHandle);
    }

    // Link the bootstrapExit method to the CallSite
    public static CallSite generateExitCallSite() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        CallSite callSite = new ConstantCallSite(bootstrapExit());
        return callSite;
    }
}