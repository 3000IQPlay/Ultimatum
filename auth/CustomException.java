package auth;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;

// This sh1t has not been tested and prob doesnt even work

public class CustomException {

    // bootstrap method to generate the MethodHandle for throwing IllegalStateException
    public static CallSite bootstrapIllegalStateException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findConstructor(IllegalStateException.class, MethodType.methodType(void.class, String.class));
        return new ConstantCallSite(mh.asType(MethodType.methodType(Throwable.class, String.class)));
    }

    // Link the bootstrap method to the CallSite
    public static CallSite generateIllegalStateExceptionCallSite() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new ConstantCallSite(bootstrapIllegalStateException());
    }

    public static void main(String[] args) throws Throwable {
        // Using the generated CallSite to throw IllegalStateException
        CallSite callSite = generateIllegalStateExceptionCallSite();
        MethodHandle methodHandle = callSite.dynamicInvoker();
        methodHandle.invokeExact("This Application is not licensed to you!");
    }
}
