package hr.java.clientcommunication.utils;

import hr.java.clientcommunication.exception.RepositoryAccessException;

public class DatabaseAccessThreadManager {
    private boolean accessInProgress = false;

    /**
     * Acquires access to the database. Blocks if another thread is using it.
     * @throws RepositoryAccessException if the thread is interrupted while waiting
     */
    public synchronized void acquireAccess() throws RepositoryAccessException {
        while (accessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RepositoryAccessException("Thread interrupted while waiting for DB access", e);
            }
        }
        accessInProgress = true;
    }

    /**
     * Releases the previously acquired database access lock.
     */
    public synchronized void releaseAccess() {
        accessInProgress = false;
        notifyAll();
    }
}
