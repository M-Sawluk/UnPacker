package exception;

import log.AppLogger;
import org.apache.log4j.Logger;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Logger logger = AppLogger.getLogger();
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error(e.getCause().getCause().getMessage());
    }
}
