package auth;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JarSizeChecker {
    public static void checkSize() {
		try {
			// Read the expected size from URL
            URL url = new URL("https://pastebin.com/123");
            URLConnection connection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String jarSizeString = reader.readLine();
            int targetJarSize = Integer.parseInt(jarSizeString);

            // Get the size of the running jar file
            URL currentJar = JarSizeChecker.class.getProtectionDomain().getCodeSource().getLocation();
            long currentJarSizeInBytes = currentJar.openConnection().getContentLength();
            int currentJarSizeInKilobytes = (int) (currentJarSizeInBytes / 1024);

            // Compare the sizes
            if (targetJarSize < currentJarSizeInKilobytes) {
				Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);
                // System.out.println("Current jar is larger than the target jar (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
            } else if (targetJarSize > currentJarSizeInKilobytes) {
				Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);
                // System.out.println("Current jar is smaller than the target jar (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
            } else {
                // System.out.println("Current jar is equal to the target jar (current: " + currentJarSizeInKilobytes + " KB, target: " + targetJarSize + " KB)");
            }

        } catch (IOException e) {
            // System.out.println("An error occurred: " + e.getMessage());
        }
	}
}