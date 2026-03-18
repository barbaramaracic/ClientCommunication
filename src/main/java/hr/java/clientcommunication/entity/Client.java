package hr.java.clientcommunication.entity;

import hr.java.clientcommunication.interfaces.Contactable;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a client in the system.
 * A client is a person who can be contacted and has associated contact preferences and address information.
 * This class is final and cannot be extended.
 */
public final class Client extends Person implements Contactable {

    /** Client's email address. */
    private String email;

    /** Client's phone number. */
    private String phone;

    /** Client's physical address. */
    private Address address;

    /** Client's communication preferences. */
    private CommunicationPreference preference;

    /**
     * Default constructor.
     */
    public Client() {
        super();
    }

    /**
     * Builder class for constructing {@link Client} objects using a fluent API.
     */
    public static class Builder {
        private final Client client = new Client();

        /**
         * Sets the ID of the client.
         *
         * @param id the unique identifier
         * @return the current {@code Builder} instance
         */
        public Builder setId(Long id) {
            client.setId(id);
            return this;
        }

        /**
         * Sets the first name of the client.
         *
         * @param firstName the first name
         * @return the current {@code Builder} instance
         */
        public Builder setFirstName(String firstName) {
            client.setFirstName(firstName);
            return this;
        }

        /**
         * Sets the last name of the client.
         *
         * @param lastName the last name
         * @return the current {@code Builder} instance
         */
        public Builder setLastName(String lastName) {
            client.setLastName(lastName);
            return this;
        }

        /**
         * Sets the birth date of the client.
         *
         * @param birthDate the date of birth
         * @return the current {@code Builder} instance
         */
        public Builder setBirthDate(LocalDate birthDate) {
            client.setBirthDate(birthDate);
            return this;
        }

        /**
         * Sets the client's email address.
         *
         * @param email the email address
         * @return the current {@code Builder} instance
         */
        public Builder setEmail(String email) {
            client.email = email;
            return this;
        }

        /**
         * Sets the client's phone number.
         *
         * @param phone the phone number
         * @return the current {@code Builder} instance
         */
        public Builder setPhone(String phone) {
            client.phone = phone;
            return this;
        }

        /**
         * Sets the client's address.
         *
         * @param address the address
         * @return the current {@code Builder} instance
         */
        public Builder setAddress(Address address) {
            client.address = address;
            return this;
        }

        /**
         * Sets the client's communication preference.
         *
         * @param preference the communication preference
         * @return the current {@code Builder} instance
         */
        public Builder setPreference(CommunicationPreference preference) {
            client.preference = preference;
            return this;
        }

        /**
         * Builds and returns the configured {@link Client} instance.
         *
         * @return a fully initialized {@link Client}
         */
        public Client build() {
            return client;
        }
    }

    /**
     * Gets the client's communication preference.
     *
     * @return the communication preference
     */
    public CommunicationPreference getPreference() {
        return preference;
    }

    /**
     * Sets the client's communication preference.
     *
     * @param preference the communication preference to set
     */
    public void setPreference(CommunicationPreference preference) {
        this.preference = preference;
    }

    /**
     * Gets the client's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the client's email address.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the client's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the client's phone number.
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the client's address.
     *
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the client's address.
     *
     * @param address the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Returns the full name of the client by combining the first and last name.
     *
     * @return the full name as a single string
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Returns a summary of the client's preferred contact method.
     * This method should be overridden to return a meaningful value.
     *
     * @return a contact preference summary (currently empty string)
     */
    @Override
    public String getPreferredContactSummary() {
        return "";
    }

    @Override
    public String toString() {
        return "Client: " + getFirstName() + ", " +
                getLastName() + ", " +
                getBirthDate() + ", " +
                email + ", " +
                phone + ", " +
                (address != null ? address.toSimpleString() : "no address") + ", " +
                (preference != null ? preference.toString() : "no preference");
    }

    public String toSimpleString() {
        return getFirstName() + " " + getLastName() + " (" + getBirthDate() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(email, client.email) && Objects.equals(phone, client.phone) && Objects.equals(address, client.address) && Objects.equals(preference, client.preference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phone, address, preference);
    }
}
