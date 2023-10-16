package auth;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;

public class JarHashChecker {
    public static String calculateHash(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        byte[] encodedhash = digest.digest();
        return bytesToHex(encodedhash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String getRemoteHash(String host, int port) throws IOException {
        try (Socket socket = new Socket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            return reader.readLine();
        }
    }

    public static boolean checkHash(String host, int port) throws IOException, NoSuchAlgorithmException {
        URL jarLocation = JarHashChecker.class.getProtectionDomain().getCodeSource().getLocation();
        try (InputStream jarStream = jarLocation.openStream()) {
            String localHash = calculateHash(jarStream);
            String remoteHash = getRemoteHash(host, port);
            return localHash.equals(remoteHash);
        }
    }
}

