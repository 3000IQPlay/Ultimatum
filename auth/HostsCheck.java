package auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class HostsCheck {

    public HostsCheck() throws FileNotFoundException {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            File file = new File("C:\\Windows\\System32\\drivers\\etc", "hosts");

            // Checks if "hosts" file exists
            if (file.exists()) {
                Scanner reader = new Scanner(file);

                // If next line in "hosts" file exists, continues with check.
                while (reader.hasNextLine()) {
                    // Reads next line
                    String data = reader.nextLine();
                    if (data.toLowerCase().contains("yourclientdomain.com") || data.toLowerCase().contains("177.243.45.153 (IP OF YOUR AUTH SERVER)")) {
                        // Informs the developers about a suspicious activity
                        WebhookInformer.sendFlag("- " + data + " has been found in Hosts file (Windows)");

                        // Create a CallSite
                        CallSite callSite = generateExitCallSite();

                        // Invoke the "exit()" method using the CallSite
                        callSite.invokeInt(0);
                    }
                }
                reader.close();
            }
        }
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
