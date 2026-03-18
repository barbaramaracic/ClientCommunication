package hr.java.clientcommunication.entity;

/**
 * Manages the user session by storing information about the currently logged-in user.
 * Provides methods to set, get, clear the logged-in user and check the user's role.
 */
public class UserSession {

    /** The currently logged-in user */
    private static User loggedInUser;

    /** Private constructor to prevent instantiation */
    private UserSession() {
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user the user to set as logged in
     */
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the logged-in user, or null if no user is logged in
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Clears the current user session.
     * After this, no user is considered logged in.
     */
    public static void clearSession() {
        loggedInUser = null;
    }

    /**
     * Checks if the currently logged-in user has the "ADMIN" role.
     *
     * @return true if a user is logged in and has role ADMIN, false otherwise
     */
    public static boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getRole() != null &&
                loggedInUser.getRole().name().equalsIgnoreCase("ADMIN");
    }
}
