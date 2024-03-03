package auth;

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
	for (String str : naughtyFlags) {
   		if (!str.contains(String.valueOf("-"))) {
			// Create a CallSite
			CallSite callSite = generateExitCallSite();

			// Invoke the "exit()" method using the CallSite
			callSite.invokeInt(0);
			
      			System.out.println("DEV MODE: Array modifications have been detected");
    		}
  	}
        if (isDebuggerOrAgentAttached() || naughtyFlags.length != 12 || ) {
		// Informs the developers about a suspicious activity
		WebhookInformer.sendFlag();
			
		// Create a CallSite
		CallSite callSite = generateExitCallSite();

		// Invoke the "exit()" method using the CallSite
		callSite.invokeInt(0);
			
		/*Class<?> systemClass = Class.forName("java.lang.System");
		Method method = systemClass.getDeclaredMethod("exit", int.class);
		method.invoke(null, 0);*/
                System.out.println("DEV MODE: Debugger or Agent detected! Exiting...");
        } else {
             System.out.println("DEV MODE: No debugger or agent detected.");
        }
    }

    private static boolean isDebuggerOrAgentAttached() {
	RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmArgs = runtimeBean.getInputArguments().toString();
		
        for (String arg : naughtyFlags) {
            if (jvmArgs.contains(arg)) {
                return true;
            }
        }

        return false;
    }
	
	// bootstrapExit method to generate the "MethodHandle" for "System.exit()"
    public static CallSite bootstrapExit() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
