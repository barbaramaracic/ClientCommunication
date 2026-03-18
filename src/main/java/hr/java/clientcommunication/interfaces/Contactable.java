package hr.java.clientcommunication.interfaces;

import hr.java.clientcommunication.entity.Client;

/**
 * A sealed interface representing entities that can be contacted.
 * <p>
 * This interface permits only the {@link Client} class to implement it.
 * Implementing classes must provide a summary of their preferred contact method.
 */
public sealed interface Contactable permits Client {

    /**
     * Returns a summary describing the preferred contact method(s) for the entity.
     *
     * @return a string summarizing the preferred contact details
     */
    String getPreferredContactSummary();
}
