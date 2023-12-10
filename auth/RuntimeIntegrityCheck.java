package auth;

import java.io.File;
import java.security.Signature;
import java.util.jar.Manifest;
import java.util.jar.JarFile;

public class RuntimeIntegrityCheck {
	public static void integrityCheck() {
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
            // System.out.println("JAR file is invalid");
			Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);
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
        File signatureFile = new File(signatureFilePath);
        byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());

        // Create a Signature object
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Verify the JAR file signature
        boolean isVerified = signature.verify(jarFileBytes, signatureBytes);

        return isVerified;
    }
}