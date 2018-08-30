

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class handles the responding of non existent services or resources as requested, in a log file using the Logger class in Java.
 *
 * ENHANCEMENT: Logging - each time requests are made, log them to a file, indicating date/time request type, response code etc.
 */

public class LoggingFile {
    private Logger mLogger;

    /**
     * The following constructor configures the handler and formatter.
     *
     * @param filePath the path where the log file has to be stored, containing all the logging information.
     * @throws IOException IOExtension
     */
    public LoggingFile(String filePath) throws IOException {
        mLogger = Logger.getLogger("LoggingFile");
        FileHandler mFileHandler = new FileHandler(filePath);
        mFileHandler.setFormatter(new SimpleFormatter());
        mLogger.addHandler(mFileHandler);
    }

    /**
     * Method to log a message into the log file.
     *
     * @param message message information.
     */
    public void loggingMessage(String message) {
        mLogger.info(message);
    }

    /**
     * Method to log a request into the log file.
     *
     * @param requestCode request code information
     * @param inetAddress inet address information
     */
    public void loggingRequest(String requestCode, InetAddress inetAddress) {
        mLogger.info("Receiving request " + requestCode + " from the address " + inetAddress);
    }

    /**
     * Method to log a response into the log file.
     *
     * @param responseCode request code information
     * @param inetAddress inet address information
     */
    public void loggingRespond(String responseCode, InetAddress inetAddress) {
        mLogger.info("Responding response " + responseCode + " to address " + inetAddress);
    }

    /**
     * Method to log a warning into the log file.
     *
     * @param warningMessage warning message information
     */
    public void loggingWarning(String warningMessage) {
        mLogger.warning(warningMessage);
    }

    /**
     * Method to log a Severe messgae into the log file.
     *
     * @param severeMessage warning message information
     */
    public void loggingSevereMsg(String severeMessage) {
        mLogger.severe(severeMessage);
    }
}
