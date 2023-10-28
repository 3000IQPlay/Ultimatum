package auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;

import net.vpnblocker.api.*;

public class VPNDetectorWAPI {
    
    private String getPublicIP() {
        String ip = "";
        try {
            URL url = new URL("https://checkip.amazonaws.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = br.readLine().trim();
        } catch (Exception e) {
            ip = "Error: " + e.getMessage();
        }
        return ip;
    }

    public Boolean isVPN() {
        String ipToLookup = getPublicIP();
        try {
            Boolean isHostingorVPN = new VPNDetection().getResponse(ipToLookup).hostip;
            return isHostingorVPN;
        } catch (IOException e) {
            // System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public void getIPInfo() {
        String ipToLookup = getPublicIP();
        new Thread(() -> {
            try {
                VPNDetection vpn_detection = new VPNDetection();
                Response api_response = vpn_detection.getResponse(ipToLookup);
                if (api_response.status.equals("success")) {
                    // System.out.println("Package: " + api_response.getPackage);
                    if (api_response.getPackage.equals("Free")) {
                        // System.out.println("Remaining Requests: " + api_response.remaining_requests);
                    }
                    // System.out.println("IP Address: " + api_response.ipaddress);
                    // System.out.println("Is this IP a VPN or Hosting Network? " + api_response.hostip);
                    // System.out.println("Organisation: " + api_response.org);
                    if (api_response.country != null) {
                        // System.out.println("Country: " + api_response.country.name);
                    }
                } else {
                    // System.out.println("Error: " + api_response.msg);
                }
            } catch (IOException ex) {
                // System.out.println("Error: " + ex.getMessage());
            }
        }).start();
    }
}
