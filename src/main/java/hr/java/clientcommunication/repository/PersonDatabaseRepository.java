package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.Person;
import hr.java.clientcommunication.exception.RepositoryAccessException;
import hr.java.clientcommunication.utils.DatabaseManager;

import java.io.IOException;
import java.sql.*;

public class PersonDatabaseRepository {

    public Long save(Person person) throws RepositoryAccessException {
        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO person(first_name, last_name, birth_date) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, person.getFirstName());
            stmt.setString(2, person.getLastName());
            stmt.setDate(3, Date.valueOf(person.getBirthDate()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RepositoryAccessException("Failed to retrieve generated person ID");
                }
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to save person", e);
        }
    }

    public void update(Person person) throws RepositoryAccessException {
        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE person SET first_name = ?, last_name = ?, birth_date = ? WHERE id = ?")) {

            stmt.setString(1, person.getFirstName());
            stmt.setString(2, person.getLastName());
            stmt.setDate(3, Date.valueOf(person.getBirthDate()));
            stmt.setLong(4, person.getId());

            stmt.executeUpdate();

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to update person", e);
        }
    }

    public void deleteById(Long id, Connection connection) throws RepositoryAccessException {
        try (connection;
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM person WHERE id = ?")) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryAccessException("Failed to delete person", e);
        }
    }

    public Person loadPersonData(Long id, Person personInstance) throws RepositoryAccessException {
        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT first_name, last_name, birth_date FROM person WHERE id = ?")) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    personInstance.setId(id);
                    personInstance.setFirstName(rs.getString("first_name"));
                    personInstance.setLastName(rs.getString("last_name"));
                    personInstance.setBirthDate(rs.getDate("birth_date").toLocalDate());
                    return personInstance;
                } else {
                    return null;
                }
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to retrieve person", e);
        }
    }
}
