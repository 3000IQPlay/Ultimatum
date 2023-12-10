package auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.jar.JarFile;

public class JarHashChecker {

    public static void checkHash() {
        // Get the current JAR file's path
        String jarFilePath = JarHashChecker.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        // Get the link to the expected hash
        String hashLink = "https://pastebin.com/123";

        // Download the expected hash from the link
        URL hashURL = new URL(hashLink);
        HttpURLConnection connection = (HttpURLConnection) hashURL.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String expectedHash = reader.readLine();

        // Calculate the actual hash of the JAR file
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        byte[] fileBytes = readFileBytes(jarFilePath);
        byte[] hash = md5Digest.digest(fileBytes);

        // Convert the hash to a string
        String actualHash = new String(hash);

        // Check if the actual hash matches the expected hash
		if (expectedHash != null) {
			if (actualHash.equals(expectedHash)) {
				// System.out.println("JAR file is intact. Hash matches expected value: " + actualHash);
			} else {
				Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);
				// System.out.println("WARNING: JAR file has been modified. Expected hash: " + expectedHash + ", Actual hash: " + actualHash);
			}
		} else {
			Class<?> systemClass = Class.forName("java.lang.System");
			Method method = systemClass.getDeclaredMethod("exit", int.class);
			method.invoke(null, 0);
			// System.out.println("ERROR: Hash is null);
		}
    }

    private static byte[] readFileBytes(String filePath) throws IOException {
        try (JarFile jarFile = new JarFile(filePath)) {
            return jarFile.getInputStream(jarFile.getManifest()).readAllBytes();
        }
    }
}

