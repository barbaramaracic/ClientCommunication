package hr.java.clientcommunication.entity;

import hr.java.clientcommunication.enumeration.UserRole;
import java.time.LocalDate;

/**
 * Represents a user in the system with authentication and role information.
 * Extends the {@link Person} class.
 */
public class User extends Person {

    /** The user's username */
    private String username;

    /** The hashed password for authentication */
    private String hashpassword;

    /** The user's role within the system */
    private UserRole role;

    /**
     * Constructs a User with specified id, personal details, username, hashed password, and role.
     *
     * @param id           the unique identifier
     * @param firstName    the user's first name
     * @param lastName     the user's last name
     * @param birthDate    the user's birth date
     * @param username     the username for login
     * @param hashpassword the hashed password for authentication
     * @param role         the user's role
     */
    public User(Long id, String firstName, String lastName, LocalDate birthDate, String username, String hashpassword, UserRole role) {
        super(id, firstName, lastName, birthDate);
        this.username = username;
        this.hashpassword = hashpassword;
        this.role = role;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the hashed password.
     *
     * @return the hashed password
     */
    public String getHashpassword() {
        return hashpassword;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the hashed password.
     *
     * @param hashpassword the hashed password to set
     */
    public void setHashpassword(String hashpassword) {
        this.hashpassword = hashpassword;
    }

    /**
     * Returns the user role.
     *
     * @return the role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets the user role.
     *
     * @param role the role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }


}
