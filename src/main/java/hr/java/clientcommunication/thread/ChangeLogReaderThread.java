package hr.java.clientcommunication.thread;

import hr.java.clientcommunication.record.ChangeLog;
import hr.java.clientcommunication.utils.ChangeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChangeLogReaderThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ChangeLogReaderThread.class);
    private List<ChangeLog> readLogs;

    /**
     * Returns the logs read by the thread after it's finished.
     * You can access this from outside, after joining the thread.
     */
    public List<ChangeLog> getReadLogs() {
        return readLogs;
    }

    @Override
    public void run() {
        try {
            readLogs = ChangeManager.loadUserActions();
            logger.info("Successfully loaded {} change logs.", readLogs.size());
            for (ChangeLog log : readLogs) {
                logger.info("Read log: {}", log);
            }
        } catch (Exception e) {
            logger.error("Error reading change logs in thread", e);
        }
    }
}
