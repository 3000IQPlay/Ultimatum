package auth;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;

public class JarHashChecker {
    public static long calculateSize(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[8192];
		long size = 0;
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			size += bytesRead;
		}
		return size;
	}

    public static String getRemoteSize(String host, int port) throws IOException {
        try (Socket socket = new Socket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            return reader.readLine();
        }
    }

    public static boolean checkSize(String host, int port) throws IOException {
		URL jarLocation = JarSizeChecker.class.getProtectionDomain().getCodeSource().getLocation();
		try (InputStream jarStream = jarLocation.openStream()) {
			long localSize = calculateSize(jarStream);
			long remoteSize = getRemoteSize(host, port);
			return localSize == remoteSize;
		}
	}
}