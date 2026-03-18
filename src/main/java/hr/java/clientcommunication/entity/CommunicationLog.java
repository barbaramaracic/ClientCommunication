package hr.java.clientcommunication.entity;

import hr.java.clientcommunication.enumeration.ContactType;
import hr.java.clientcommunication.interfaces.Loggable;

import java.time.LocalDateTime;

/**
 * Represents a record of communication with a client.
 * Stores details such as which client was contacted, by which user, the type of contact,
 * date and time of the communication, whether it was successful, and any additional notes.
 */
public class CommunicationLog extends Entity implements Loggable {

    /** The client involved in the communication. */
    private Client client;

    /** The user responsible for the communication. */
    private User userResponsible;

    /** The type of contact used (e.g., email, phone). */
    private ContactType type;

    /** The date and time when the communication occurred. */
    private LocalDateTime dateTime;

    /** Indicates if the communication was successful. */
    private boolean success;

    /** Additional notes regarding the communication. */
    private String note;

    /**
     * Constructs a new CommunicationLog with all fields initialized.
     *
     * @param id the unique identifier
     * @param client the client involved in communication
     * @param userResponsible the user responsible for communication
     * @param type the contact type used
     * @param dateTime the date and time of communication
     * @param success whether the communication was successful
     * @param note any additional notes
     */
    public CommunicationLog(Long id, Client client, User userResponsible, ContactType type,
                            LocalDateTime dateTime, boolean success, String note) {
        super(id);
        this.client = client;
        this.userResponsible = userResponsible;
        this.type = type;
        this.dateTime = dateTime;
        this.success = success;
        this.note = note;
    }

    /**
     * Default no-argument constructor.
     */
    public CommunicationLog() {
    }

    /**
     * Returns the client involved in this communication.
     *
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the client involved in this communication.
     *
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Returns the user responsible for the communication.
     *
     * @return the user responsible
     */
    public User getUserResponsible() {
        return userResponsible;
    }

    /**
     * Sets the user responsible for the communication.
     *
     * @param userResponsible the user to set
     */
    public void setUserResponsible(User userResponsible) {
        this.userResponsible = userResponsible;
    }

    /**
     * Returns the contact type used for the communication.
     *
     * @return the contact type
     */
    public ContactType getType() {
        return type;
    }

    /**
     * Sets the contact type used for the communication.
     *
     * @param type the contact type to set
     */
    public void setType(ContactType type) {
        this.type = type;
    }

    /**
     * Returns the date and time of the communication.
     *
     * @return the date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time of the communication.
     *
     * @param dateTime the date and time to set
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns whether the communication was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the communication.
     *
     * @param success true if successful, false otherwise
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns the notes related to the communication.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the notes related to the communication.
     *
     * @param note the notes to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Returns a formatted string describing the details of this communication log.
     *
     * @return the log details as a string
     */
    @Override
    public String getLogDetails() {
        return String.format("Communication Log: Contacted client %s via %s on %s by %s. Success: %s. Note: %s",
                client.getFullName(),
                type,
                dateTime,
                userResponsible.getUsername(),
                success ? "YES" : "NO",
                note);
    }
}
