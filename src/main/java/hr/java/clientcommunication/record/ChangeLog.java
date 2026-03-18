package hr.java.clientcommunication.record;

import hr.java.clientcommunication.enumeration.UserRole;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a log entry recording changes made by a user.
 * Contains information about the user, their role,
 * the old and new values, and the timestamp of the change.
 *
 * @param user      the username of the user who made the change
 * @param role      the role of the user as a string
 * @param oldValue  the previous value before the change
 * @param newValue  the new value after the change
 * @param timestamp the date and time when the change was made
 */
public record ChangeLog(
        String user,
        String role,
        String oldValue,
        String newValue,
        LocalDateTime timestamp
) implements Serializable {

    /**
     * Constructs a ChangeLog instance with the current timestamp.
     *
     * @param user     the username of the user who made the change
     * @param role     the role of the user as a UserRole enum
     * @param oldValue the previous value before the change
     * @param newValue the new value after the change
     */
    public ChangeLog(String user, UserRole role, String oldValue, String newValue) {
        this(user, String.valueOf(role), oldValue, newValue, LocalDateTime.now());
    }

    /**
     * Returns a string representation of the ChangeLog entry.
     *
     * @return a formatted string containing timestamp, user, and role
     */
    @Override
    public String toString() {
        return "[" + timestamp + "] " + user + " (" + role + ") created/changed something."
                + (newValue != null ? " New: " + newValue : "")
                + (oldValue != null ? " Old: " + oldValue : "");
    }
}
