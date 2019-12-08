package pia.tools;

import org.apache.log4j.spi.LoggerFactory;

public class Logger {
    org.apache.log4j.Logger logger;
    public Logger(Class type) {
        logger = org.apache.log4j.Logger.getLogger(type);
    }

    public void error(String message, Throwable e) {
        logger.error(message, e);
    }
}
