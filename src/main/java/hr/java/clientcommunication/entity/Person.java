package hr.java.clientcommunication.entity;

import java.time.LocalDate;

/**
 * Abstract class representing a person with basic personal information.
 * Inherits from {@link Entity}.
 */
public abstract class Person extends Entity {

    /** The person's first name */
    private String firstName;

    /** The person's last name */
    private String lastName;

    /** The person's date of birth */
    private LocalDate birthDate;

    /**
     * Constructs a Person with specified id, first name, last name, and birth date.
     *
     * @param id         the unique identifier
     * @param firstName  the first name
     * @param lastName   the last name
     * @param birthDate  the birth date
     */
    protected Person(Long id, String firstName, String lastName, LocalDate birthDate) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    /**
     * Default no-argument constructor.
     */
    protected Person() {
    }

    /**
     * Returns the first name of the person.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the person.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the person.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the person.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the birth date of the person.
     *
     * @return the birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date of the person.
     *
     * @param birthDate the birth date to set
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
