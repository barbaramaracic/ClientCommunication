package hr.java.clientcommunication.utils;

import hr.java.clientcommunication.record.ChangeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChangeManager {
    private ChangeManager() {}

    private static final String FILE_PATH = "dat/changes.bin";
    private static final Logger logger = LoggerFactory.getLogger(ChangeManager.class);
    private static boolean accessInProgress = false;

    private static synchronized void waitForAccess() {
        while (accessInProgress) {
            try {
                ChangeManager.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread interrupted while waiting for access", e);
            }
        }
        accessInProgress = true;
    }

    private static synchronized void releaseAccess() {
        accessInProgress = false;
        ChangeManager.class.notifyAll();
    }

    // 👇 javna metoda za čitanje
    public static synchronized  List<ChangeLog> loadUserActions() {
        waitForAccess();
        try {
            return loadUserActionsUnsafe();
        } finally {
            releaseAccess();
        }
    }

    // 👇 privatna metoda bez zaključavanja (koristi se iznutra)
    private static List<ChangeLog> loadUserActionsUnsafe() {
        List<ChangeLog> userActions = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            logger.warn("File doesn't exist or is empty: {}", FILE_PATH);
            return userActions;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            userActions = (List<ChangeLog>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Deserialization error: {}", e.getMessage(), e);
        }
        return userActions;
    }

    // 👇 javna metoda za spremanje
    public static void saveUserAction(List<ChangeLog> promjene) {
        waitForAccess();
        try {
            saveUserActionUnsafe(promjene);
        } finally {
            releaseAccess();
        }
    }

    private static void saveUserActionUnsafe(List<ChangeLog> promjene) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(promjene);
            logger.info("Change saved.");
        } catch (IOException e) {
            logger.error("Serialization error.", e);
        }
    }

    // 👇 ovo je sada sigurno jer koristi "unsafe" metode
    public static synchronized void  addNewChange(ChangeLog change) {
        waitForAccess();
        try {
            List<ChangeLog> promjene = loadUserActionsUnsafe(); // ne zove waitForAccess ponovno
            promjene.add(change);
            saveUserActionUnsafe(promjene);
        } finally {
            releaseAccess();
        }
    }
}
