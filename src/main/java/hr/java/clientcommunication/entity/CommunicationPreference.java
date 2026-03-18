package hr.java.clientcommunication.entity;

/**
 * Represents a client's communication preferences,
 * indicating which types of communication (email, SMS, phone call) are allowed.
 */
public class CommunicationPreference extends Entity {

    /** Indicates if email communication is allowed. */
    private boolean emailAllowed;

    /** Indicates if SMS communication is allowed. */
    private boolean smsAllowed;

    /** Indicates if phone call communication is allowed. */
    private boolean phoneCallAllowed;

    /**
     * Constructs a CommunicationPreference with specified preferences.
     *
     * @param id the unique identifier
     * @param emailAllowed whether email communication is allowed
     * @param smsAllowed whether SMS communication is allowed
     * @param phoneCallAllowed whether phone call communication is allowed
     */
    public CommunicationPreference(Long id, boolean emailAllowed, boolean smsAllowed, boolean phoneCallAllowed) {
        super(id);
        this.emailAllowed = emailAllowed;
        this.smsAllowed = smsAllowed;
        this.phoneCallAllowed = phoneCallAllowed;
    }

    /**
     * Default no-argument constructor.
     */
    public CommunicationPreference() {
    }

    /**
     * Returns whether email communication is allowed.
     *
     * @return true if email communication is allowed, false otherwise
     */
    public boolean isEmailAllowed() {
        return emailAllowed;
    }

    /**
     * Sets whether email communication is allowed.
     *
     * @param emailAllowed true to allow email communication, false to disallow
     */
    public void setEmailAllowed(boolean emailAllowed) {
        this.emailAllowed = emailAllowed;
    }

    /**
     * Returns whether SMS communication is allowed.
     *
     * @return true if SMS communication is allowed, false otherwise
     */
    public boolean isSmsAllowed() {
        return smsAllowed;
    }

    /**
     * Sets whether SMS communication is allowed.
     *
     * @param smsAllowed true to allow SMS communication, false to disallow
     */
    public void setSmsAllowed(boolean smsAllowed) {
        this.smsAllowed = smsAllowed;
    }

    /**
     * Returns whether phone call communication is allowed.
     *
     * @return true if phone call communication is allowed, false otherwise
     */
    public boolean isPhoneCallAllowed() {
        return phoneCallAllowed;
    }

    /**
     * Sets whether phone call communication is allowed.
     *
     * @param phoneCallAllowed true to allow phone call communication, false to disallow
     */
    public void setPhoneCallAllowed(boolean phoneCallAllowed) {
        this.phoneCallAllowed = phoneCallAllowed;
    }

    @Override
    public String toString() {
        return "CommunicationPreference{" +
                "emailAllowed=" + emailAllowed +
                ", smsAllowed=" + smsAllowed +
                ", phoneCallAllowed=" + phoneCallAllowed +
                '}';
    }
}
