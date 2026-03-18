package hr.java.clientcommunication.entity;

import java.util.Objects;

/**
 * Represents a physical address with street, house number, city, and postal code.
 * Inherits from the base {@link Entity} class.
 */
public class Address extends Entity {

    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;

    /**
     * Default constructor.
     */
    public Address() {
        super();
    }

    /**
     * Builder class for constructing {@link Address} objects using a fluent API.
     */
    public static class Builder {

        private final Address address = new Address();

        /**
         * Sets the street name.
         *
         * @param street the name of the street
         * @return the current {@code Builder} instance
         */
        public Builder setStreet(String street) {
            address.street = street;
            return this;
        }

        /**
         * Sets the house number.
         *
         * @param houseNumber the house number
         * @return the current {@code Builder} instance
         */
        public Builder setHouseNumber(String houseNumber) {
            address.houseNumber = houseNumber;
            return this;
        }

        /**
         * Sets the city name.
         *
         * @param city the name of the city
         * @return the current {@code Builder} instance
         */
        public Builder setCity(String city) {
            address.city = city;
            return this;
        }

        /**
         * Sets the postal code.
         *
         * @param postalCode the postal code
         * @return the current {@code Builder} instance
         */
        public Builder setPostalCode(String postalCode) {
            address.postalCode = postalCode;
            return this;
        }

        /**
         * Builds and returns the configured {@link Address} instance.
         *
         * @return a fully initialized {@link Address}
         */
        public Address build() {
            return address;
        }
    }

    /**
     * Gets the street name.
     *
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street name.
     *
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the house number.
     *
     * @return the house number
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * Sets the house number.
     *
     * @param houseNumber the house number to set
     */
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * Gets the city name.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city name.
     *
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code.
     *
     * @param postalCode the postal code to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Checks equality based on street, house number, city, and postal code.
     *
     * @param o the object to compare
     * @return {@code true} if the objects represent the same address, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(houseNumber, address.houseNumber) &&
                Objects.equals(city, address.city) &&
                Objects.equals(postalCode, address.postalCode);
    }

    /**
     * Computes the hash code based on all address fields.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(street, houseNumber, city, postalCode);
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }

    public String toSimpleString() {
        return street+ ", " + houseNumber + ", " + city + ", " + postalCode;
    }
}
