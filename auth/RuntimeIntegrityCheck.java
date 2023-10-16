package auth;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class RuntimeIntegrityCheck {
    public static void integrityCheck(String host, String port) {
        try {
			Socket socket = new Socket(host, port);
            String originalChecksum = getOriginalChecksum(socket);
            String currentChecksum = getCurrentChecksum();
            if (!originalChecksum.equals(currentChecksum)) {
				Class<?> systemClass = Class.forName("java.lang.System");
				Method method = systemClass.getDeclaredMethod("exit", int.class);
				method.invoke(null, 0);
                // System.out.println("Application file has been tampered with! Exiting...");
                // Continue with the rest of your code
            } else {
                // System.out.println("Application file is intact.");
                // Continue with the rest of your code
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getOriginalChecksum(String host, String port) {
        try {
			Socket socket = new Socket(host, port);
            InputStream inputStream = socket.getInputStream();
            String originalChecksum = calculateChecksum(inputStream);
            return originalChecksum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getCurrentChecksum() throws URISyntaxException, Exception {
        String jarPath = RuntimeIntegrityCheck.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        InputStream inputStream = new FileInputStream(jarPath);
        return calculateChecksum(inputStream);
    }

    private static String calculateChecksum(InputStream inputStream) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        DigestInputStream dis = new DigestInputStream(inputStream, md);
        byte[] buffer = new byte[1024];
        while (dis.read(buffer) != -1) {
            // Reading from the stream updates the digest
        }
        byte[] digest = md.digest();
        return bytesToHex(digest);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}