package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
    public static EnumOS getOSType() {
        String s2 = System.getProperty("os.name").toLowerCase();
        return s2.contains("win") ? EnumOS.WINDOWS : (s2.contains("mac") ? EnumOS.OSX : (s2.contains("solaris") ? EnumOS.SOLARIS : (s2.contains("sunos") ? EnumOS.SOLARIS : (s2.contains("linux") ? EnumOS.LINUX : (s2.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN)))));
    }

    public static <V> V runTask(FutureTask<V> task, Logger logger) {
        try {
            task.run();
            return task.get();
        }
        catch (ExecutionException executionexception) {
            logger.fatal("Error executing task", (Throwable)executionexception);
            if (executionexception.getCause() instanceof OutOfMemoryError) {
                OutOfMemoryError outofmemoryerror = (OutOfMemoryError)executionexception.getCause();
                throw outofmemoryerror;
            }
        }
        catch (InterruptedException interruptedexception) {
            logger.fatal("Error executing task", (Throwable)interruptedexception);
        }
        return null;
    }

    public static enum EnumOS {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN;

    }
}

