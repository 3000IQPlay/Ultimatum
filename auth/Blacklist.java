package auth;

import auth.WebhookInformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Arrays;

import java.util.List;

public class Blacklist {
    public static void checkOffline() throws InterruptedException, IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) return;
		
        List<String> blacklistedHWIDs = Arrays.asList(
                "A4C82042-B56D-E950-B8C4-E4FF9378B252");
		
        String[] blacklistedNames = {
                "WDAGUtilityAccount"
        };
		
        String[] blacklistedProcceses = {
                "httpdebuggerui.exe", "wireshark.exe", "fiddler.exe", "taskmgr.exe", "vboxservice.exe", "df5serv.exe",
		"processhacker.exe", "vboxtray.exe", "vmtoolsd.exe", "vmwaretray", "vmacthlp.exe", "vmsrvc.exe", "vmusrvc.exe",
                "ida64.exe", "ollydbg.exe", "pestudio.exe", "vmwareuser.exe", "vgauthservice.exe",
                "x96dbg.exe", "x32dbg.exe", "prl_cc.exe", "prl_tools.exe", "xenservice.exe", "qemu-ga.exe",
                "joeboxcontrol.exe", "ksdumperclient.exe", "ksdumper.exe", "joeboxserver.exe"};
				
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new ProcessBuilder("tasklist").start().getInputStream()));
        String line;
		
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        try {
            String processes = builder.toString();
            String host = InetAddress.getLocalHost().getHostName();
            for (String process : blacklistedProcceses) {
                if (processes.contains(process)) {
                    // System.out.println("DEV MODE: A blacklisted process has been detected -> " + process);
					
                    // Informs the developers about a suspicious activity
					WebhookInformer.sendFlag("- Blacklisted process has been detected -> " + process);
			
					// Create a CallSite
					CallSite callSite = generateExitCallSite();

					// Invoke the "exit()" method using the CallSite
					callSite.invokeInt(0);
                }
            }
            for (String name : blacklistedNames) {
                if (name.equals(host)) {
                    // System.out.println("DEV MODE: A blacklisted User name has been detected -> " + name);
					
                    // Informs the developers about a suspicious activity
					WebhookInformer.sendFlag("- Blacklisted User Name has been detected -> " + name);
			
					// Create a CallSite
					CallSite callSite = generateExitCallSite();

					// Invoke the "exit()" method using the CallSite
					callSite.invokeInt(0);
                }
            }
        } catch (IOException e) {} catch (ClassNotFoundException e) {
			// Empty
        }
    }
	
	public static void checkOnline() throws InterruptedException, IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		// SoonTM
    }
	
	// bootstrapExit method to generate the "MethodHandle" for "System.exit()"
    public static CallSite bootstrapExit() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class<?> klass = Class.forName("java.lang.System");
        MethodHandle methodHandle = MethodHandles.lookup().findStaticMethod(klass, "exit", int.class);
        return new ConstantCallSite(methodHandle);
    }

    // Link the bootstrapExit method to the CallSite
    public static CallSite generateExitCallSite() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CallSite callSite = new ConstantCallSite(bootstrapExit());
        return callSite;
    }
}
