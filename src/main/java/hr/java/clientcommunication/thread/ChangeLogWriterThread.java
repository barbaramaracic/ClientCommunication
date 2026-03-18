package hr.java.clientcommunication.thread;

import hr.java.clientcommunication.record.ChangeLog;
import hr.java.clientcommunication.utils.ChangeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeLogWriterThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ChangeLogWriterThread.class);
    private final ChangeLog changeLog;

    public ChangeLogWriterThread(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    @Override
    public void run() {
        try {
            ChangeManager.addNewChange(changeLog);
            logger.info("Change log successfully saved: " + changeLog);
        } catch (Exception e) {
            logger.error("Error saving change log in thread", e);
        }
    }
}
