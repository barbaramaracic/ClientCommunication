package hr.java.clientcommunication.entity;

/**
 * Abstract class representing an entity with a unique identifier.
 */
public abstract class Entity {

    /** The unique identifier of the entity */
    private Long id;

    /**
     * Constructs an Entity with the specified unique identifier.
     *
     * @param id the unique identifier
     */
    protected Entity(Long id) {
        this.id = id;
    }

    /**
     * Default no-argument constructor.
     */
    protected Entity() {
    }

    /**
     * Returns the unique identifier of this entity.
     *
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this entity.
     *
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }
}
