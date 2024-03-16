package auth;

import java.io.File;
import java.nio.file.Files;
import java.net.URISyntaxException;

public class SelfDelete {

    private static void selfDestructWindowsJARFile() throws Exception {
        String currentJARFilePath = SelfDelete.getCurrentJarPath().toString();
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJARFilePath + "\"");
    }

    public static void selfDestructJARFile() throws Exception {
	String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
			System.out.println("DEV MODE: Self Destruct has been activated");
			
            SelfDelete.selfDestructWindowsJARFile();
        } else {
			System.out.println("DEV MODE: Self Destruct has been activated");
			
            File directoryFilePath = SelfDelete.getCurrentJarPath();
            Files.delete(directoryFilePath.toPath());
        }
    }
	
    public static File getCurrentJarPath() throws URISyntaxException {
        return new File(SelfDelete.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    }
}
