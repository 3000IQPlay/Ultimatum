package auth;

import auth.WebhookInformer;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.List;

public class ArgumentChecker {
	private static final String[] naughtyFlags = {
			"-agentlib:jdwp",
            "-XBootclasspath",
            "-javaagent",
            "-Xdebug",
            "-agentlib",
            "-Xrunjdwp",
            "-Xnoagent",
            "-verbose",
            "-DproxySet",
            "-DproxyHost",
            "-DproxyPort",
            "-Djavax.net.ssl.trustStore",
            "-Djavax.net.ssl.trustStorePassword"
    };

    public static void checkArgument() {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmArgs = runtimeBean.getInputArguments().toString();
		
		for (String str : naughtyFlags) {
			if (!str.contains(String.valueOf("-"))) {
				// System.out.println("DEV MODE: Blacklisted JVM Arguments List has been modified!");
			
				// Informs the developers about a suspicious activity
				WebhookInformer.sendFlag("- Blacklisted JVM Arguments List has been modified.");
				
				// Create a CallSite
				CallSite callSite = generateExitCallSite();

				// Invoke the "exit()" method using the CallSite
				callSite.invokeInt(0);
    		}
		}
		
		if (naughtyFlags.length == 13) {
			for (String arg : naughtyFlags) {
				if (jvmArgs.contains(arg)) {
					// System.out.println("DEV MODE: Blacklisted JVM Argument has been detected!");
				
					// Informs the developers about a suspicious activity
					WebhookInformer.sendFlag("- Bad Argument has been detected -> " + arg);
			
					// Create a CallSite
					CallSite callSite = generateExitCallSite();

					// Invoke the "exit()" method using the CallSite
					callSite.invokeInt(0);
			
					/*Class<?> systemClass = Class.forName("java.lang.System");
					Method method = systemClass.getDeclaredMethod("exit", int.class);
					method.invoke(null, 0);*/
				} else {
					// System.out.println("DEV MODE: JVM Argument: " + arg + " has not been detected.");
				}
			}
		} else {
			// System.out.println("DEV MODE: Blacklisted JVM Arguments List has been modified!");
			
			// Informs the developers about a suspicious activity
			WebhookInformer.sendFlag("- Blacklisted JVM Arguments List has been modified.");
			
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
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
