package auth;

import auth.WebhookInformer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AntiClassCall {
    public AntiClassCall() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException, InterruptedException {
		if (!Objects.requireNonNull(getCallerClassName()).contains(AntiClassCall.class.getName())) { // Whitelist it self, calling AntiClassCall in AntiClassCall won't Crash the Program.
			if (!Objects.equals(getCallerClassName(), Main.class.getName())) { // If the class calling THIS class AnitClassCall isn't EITHER Main or AnitClassCall class, Crash.
				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					// System.out.println("DEV MODE: " + AntiClassCall.class.getName().toString() + " has been called in a non whitelisted Class");
					
					// Informs the developers about a suspicious activity
					WebhookInformer.sendFlag("- " + AntiClassCall.class.getName().toString() + " has been called in a non-whitelisted Class.");
			
					// Create a CallSite
					CallSite callSite = generateExitCallSite();

					// Invoke the "exit()" method using the CallSite
					callSite.invokeInt(0);
				}
				return;
			} else {
				// Rest of the code here.
			}
		}
	}

	private String getCallerClassName() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(AntiClassCall.class.getName())
					&& ste.getClassName().indexOf("java.lang.Thread") != 0) {
				return ste.getClassName();
			}
		}
		return null;
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
