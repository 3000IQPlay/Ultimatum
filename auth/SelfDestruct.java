package auth;

import java.io.File;
import java.nio.file.Files;
import lol.fabuls.utilities.system.SystemUtil;

public class SelfDestruct {

    private static void selfDestructWindowsJARFile() throws Exception {
        String currentJARFilePath = SelfDestruct.getCurrentJarPath().toString();
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJARFilePath + "\"");
    }

    public static void selfDestructJARFile() throws Exception {
		String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            SelfDestruct.selfDestructWindowsJARFile();
        } else {
            File directoryFilePath = SelfDestruct.getCurrentJarPath();
            Files.delete(directoryFilePath.toPath());
        }
    }
	
	public static File getCurrentJarPath() throws URISyntaxException {
        return new File(AbstractProtector.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    }
}