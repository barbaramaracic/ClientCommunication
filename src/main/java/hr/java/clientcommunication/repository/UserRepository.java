package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.exception.DuplicateUserException;
import hr.java.clientcommunication.utils.DatabaseManager;
import hr.java.clientcommunication.utils.UserFileRepository;
import hr.java.clientcommunication.utils.UserFileRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private static final String USERS_TXT_PATH = "dat/users.txt";

    public void save(User user) throws IOException {
        try (Connection connection = DatabaseManager.connectToDatabase()) {
            connection.setAutoCommit(false);

            // 1. Insert into person table
            Long generatedId = null;
            try (PreparedStatement personStmt = connection.prepareStatement(
                    "INSERT INTO person (first_name, last_name, birth_date) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                personStmt.setString(1, user.getFirstName());
                personStmt.setString(2, user.getLastName());
                personStmt.setDate(3, Date.valueOf(user.getBirthDate()));
                personStmt.executeUpdate();

                // Get generated id
                try (ResultSet rs = personStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getLong(1);
                        user.setId(generatedId);
                    } else {
                        throw new SQLException("Failed to obtain generated person ID.");
                    }
                }
            }

            // 2. Insert into user table
            try (PreparedStatement userStmt = connection.prepareStatement(
                    "INSERT INTO \"USER\" (id, username, hashpassword, role) VALUES (?, ?, ?, ?)")) {
                userStmt.setLong(1, generatedId);
                userStmt.setString(2, user.getUsername());
                userStmt.setString(3, user.getHashpassword());
                userStmt.setString(4, user.getRole().name());
                userStmt.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            throw new IOException("Database error: " + e.getMessage(), e);
        }

        // 3. Save to users.txt
        writeUserToFile(user);
    }

    private void writeUserToFile(User user) throws IOException {
        Path path = Paths.get(USERS_TXT_PATH);

        // Create directory if it doesn't exist
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // Create file if it doesn't exist
        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String line = String.format("%d;%s;%s;%s;%s;%s;%s",
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate().format(formatter),
                user.getUsername(),
                user.getHashpassword(),
                user.getRole().name()
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_TXT_PATH, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM \"USER\" WHERE username = ?";

        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException | IOException e) {
            // Log error ako imaš logger ili baci runtime exception
            e.printStackTrace();
        }
        return false; // Ako je greška, tretiraj da ne postoji
    }

    /**
     * Dohvati sve korisnike iz baze podataka.
     * @return lista svih korisnika
     * @throws IOException ako dođe do greške u bazi
     */
    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();

        String sql = "SELECT p.id, p.first_name, p.last_name, p.birth_date, u.username, u.hashpassword, u.role " +
                "FROM person p JOIN \"USER\" u ON p.id = u.id";

        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                String username = rs.getString("username");
                String hashpassword = rs.getString("hashpassword");
                UserRole role = UserRole.valueOf(rs.getString("role"));

                User user = new User(id, firstName, lastName, birthDate, username, hashpassword, role);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new IOException("Error reading users from database: " + e.getMessage(), e);
        }

        return users;
    }

    /**
     * Briše korisnika po id-u iz baze.
     * @param id id korisnika
     * @throws IOException ako dođe do greške pri brisanju
     */
    public void deleteById(Long id) throws IOException {
        String sqlDeleteUser = "DELETE FROM \"USER\" WHERE id = ?";
        String sqlDeletePerson = "DELETE FROM person WHERE id = ?";

        try (Connection connection = DatabaseManager.connectToDatabase()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteUserStmt = connection.prepareStatement(sqlDeleteUser);
                 PreparedStatement deletePersonStmt = connection.prepareStatement(sqlDeletePerson)) {

                deleteUserStmt.setLong(1, id);
                deleteUserStmt.executeUpdate();

                deletePersonStmt.setLong(1, id);
                deletePersonStmt.executeUpdate();

                UserFileRepository userutil = new UserFileRepository();
                userutil.deleteUserFromFile(id);;

                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new IOException("Error deleting user from database: " + e.getMessage(), e);
        }
    }
    /**
     * Ažurira korisnika u bazi na temelju ID-a.
     * @param user korisnik s ažuriranim podacima
     * @throws IOException ako dođe do greške prilikom ažuriranja
     */
    public void update(User user) throws IOException {
        String updatePersonSql = "UPDATE person SET first_name = ?, last_name = ?, birth_date = ? WHERE id = ?";
        String updateUserSql = "UPDATE \"USER\" SET username = ?, hashpassword = ?, role = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.connectToDatabase()) {
            connection.setAutoCommit(false);

            try (PreparedStatement personStmt = connection.prepareStatement(updatePersonSql);
                 PreparedStatement userStmt = connection.prepareStatement(updateUserSql)) {

                // Update person
                personStmt.setString(1, user.getFirstName());
                personStmt.setString(2, user.getLastName());
                personStmt.setDate(3, Date.valueOf(user.getBirthDate()));
                personStmt.setLong(4, user.getId());
                personStmt.executeUpdate();

                // Update user
                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getHashpassword());
                userStmt.setString(3, user.getRole().name());
                userStmt.setLong(4, user.getId());
                userStmt.executeUpdate();

                UserFileRepository userutil = new UserFileRepository();
                userutil.updateUserInFile(user);

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw new IOException("Failed to update user: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            throw new IOException("Database error while updating user: " + e.getMessage(), e);
        }
    }
    public Optional<User> findById(Long id) throws IOException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.birth_date, u.username, u.hashpassword, u.role " +
                "FROM person p JOIN \"USER\" u ON p.id = u.id WHERE p.id = ?";

        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                    String username = rs.getString("username");
                    String hashpassword = rs.getString("hashpassword");
                    UserRole role = UserRole.valueOf(rs.getString("role"));

                    User user = new User(id, firstName, lastName, birthDate, username, hashpassword, role);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new IOException("Error finding user by ID from database: " + e.getMessage(), e);
        }

        return Optional.empty();
    }
}
