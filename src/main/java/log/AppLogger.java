package log;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.IOException;

import static utils.PropertiesUtils.loadProperties;

public class AppLogger {
    private static final String LOGS_LOCATION = "location";
    private static Logger logger;


    private static Logger initialize()  {
        logger = Logger.getLogger(AppLogger.class);
        SimpleLayout layout = new SimpleLayout();
        FileAppender appender = null;
        try {
            appender = new FileAppender(layout,loadProperties(LOGS_LOCATION) + "\\FileBeat\\applogs.log",false);
            logger.addAppender(appender);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    public static Logger getLogger() {
        if(logger == null)
            return initialize();
        else
          return logger;
    }
}
