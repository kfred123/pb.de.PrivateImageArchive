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

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Object... params) {
        logger.error(String.format(message, params));
    }

    public void warn(String message, Object... params) {
        logger.warn(String.format(message, params));
    }

    public void info(String message, Object... params) {
        logger.info(String.format(message, params));
    }
}
