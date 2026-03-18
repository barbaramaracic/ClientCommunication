package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.Address;
import hr.java.clientcommunication.exception.RepositoryAccessException;
import hr.java.clientcommunication.exception.EmptyRepositoryResultException;
import hr.java.clientcommunication.utils.DatabaseAccessThreadManager;
import hr.java.clientcommunication.utils.DatabaseManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDatabaseRepository extends AbstractRepository<Address> {
    private static final DatabaseAccessThreadManager accessManager = new DatabaseAccessThreadManager();

    @Override
    public synchronized Address findById(Long id) throws RepositoryAccessException {
        accessManager.acquireAccess();
        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, street, house_number, city, postal_code FROM ADDRESS WHERE ID = ?")) {
            stmt.setLong(1, id);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return extractAddressFromResultSet(resultSet);
                } else {
                    throw new EmptyRepositoryResultException("Address with id " + id + " not found!");
                }
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to find address by ID", e);
        } finally {
            accessManager.releaseAccess();
        }
    }

    @Override
    public List<Address> findAll() throws RepositoryAccessException {
        List<Address> addresses = new ArrayList<>();

        try (Connection connection = DatabaseManager.connectToDatabase();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(
                     "SELECT id, street, house_number, city, postal_code FROM ADDRESS")) {

            while (resultSet.next()) {
                addresses.add(extractAddressFromResultSet(resultSet));
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }

        return addresses;
    }

    @Override
    public synchronized void save(Address address) throws RepositoryAccessException {
        accessManager.acquireAccess();

        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO ADDRESS (STREET, HOUSE_NUMBER, CITY, POSTAL_CODE) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getHouseNumber());
            stmt.setString(3, address.getCity());
            stmt.setString(4, address.getPostalCode());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    address.setId(rs.getLong(1));  // Postavljanje ID-a nakon spremanja
                } else {
                    throw new SQLException("Failed to retrieve generated address ID");
                }
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            accessManager.releaseAccess();
        }
    }

    private Address extractAddressFromResultSet(ResultSet resultSet) throws SQLException {
        Address address = new Address();
        address.setId(resultSet.getLong("id"));
        address.setStreet(resultSet.getString("street"));
        address.setHouseNumber(resultSet.getString("house_number"));
        address.setCity(resultSet.getString("city"));
        address.setPostalCode(resultSet.getString("postal_code"));

        return address;
    }

    public synchronized void update(Address address) throws RepositoryAccessException {
        if (address.getId() == null) {
            throw new RepositoryAccessException("Address ID is null, cannot update address without ID.");
        }
        accessManager.acquireAccess();
        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE ADDRESS SET street = ?, house_number = ?, city = ?, postal_code = ? WHERE id = ?")) {

            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getHouseNumber());
            stmt.setString(3, address.getCity());
            stmt.setString(4, address.getPostalCode());
            stmt.setLong(5, address.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RepositoryAccessException("No address found with ID " + address.getId() + " to update.");
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            accessManager.releaseAccess();
        }
    }

    public synchronized void deleteById(Long id, Connection connection) throws RepositoryAccessException {
        accessManager.acquireAccess();
        try (connection;
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM ADDRESS WHERE id = ?")) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch ( SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            accessManager.releaseAccess();
        }
    }
}
