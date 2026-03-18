package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.CommunicationLog;
import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.enumeration.ContactType;
import hr.java.clientcommunication.exception.RepositoryAccessException;
import hr.java.clientcommunication.record.ChangeLog;
import hr.java.clientcommunication.utils.ChangeManager;
import hr.java.clientcommunication.utils.DatabaseAccessThreadManager;
import hr.java.clientcommunication.utils.DatabaseManager;
import hr.java.clientcommunication.utils.UserFileRepository;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommunicationLogDatabaseRepository {
    private static final DatabaseAccessThreadManager accessManager = new DatabaseAccessThreadManager();

    public List<CommunicationLog> findAll() throws RepositoryAccessException {
        List<CommunicationLog> logs = new ArrayList<>();
        try (Connection conn = DatabaseManager.connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM communication_log")) {

            while (rs.next()) {
                logs.add(mapRowToLog(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return logs;
    }

    public void save(CommunicationLog log) throws RepositoryAccessException {
        String sql = """
                INSERT INTO communication_log (client_id, user_id, type, date_time, success, note)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        accessManager.acquireAccess();
        try (Connection conn = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, log.getClient().getId());
            stmt.setLong(2, log.getUserResponsible().getId());
            stmt.setString(3, log.getType().name());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getDateTime()));
            stmt.setBoolean(5, log.isSuccess());
            stmt.setString(6, log.getNote());

            stmt.executeUpdate();

            String description = log.getLogDetails();

            ChangeLog logcomm = new ChangeLog(
                    UserSession.getLoggedInUser().getUsername(),
                    UserSession.getLoggedInUser().getRole(),
                    null,
                    description
            );
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> ChangeManager.addNewChange(logcomm));
            executor.shutdown();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
        finally{accessManager.releaseAccess();}
    }

    private CommunicationLog mapRowToLog(ResultSet rs) throws SQLException, IOException {
        ClientDatabaseRepository clientRepo = new ClientDatabaseRepository();
        UserFileRepository userRepo = new UserFileRepository();

        Long id = rs.getLong("id");
        Optional<Client> client = clientRepo.findById(rs.getLong("client_id"));
        Optional<User> user = userRepo.findByIdFromFile(rs.getLong("user_id"));
        ContactType type = ContactType.valueOf(rs.getString("type"));
        LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
        boolean success = rs.getBoolean("success");
        String note = rs.getString("note");

        return new CommunicationLog(id, client.orElse(null), user.orElse(null), type, dateTime, success, note);
    }

    public void deleteById(Long id) throws RepositoryAccessException {
        String sql = "DELETE FROM communication_log WHERE id = ?";
        accessManager.acquireAccess();
        try (Connection conn = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Dohvati podatke za log prije brisanja
            Optional<CommunicationLog> logToDeleteOpt = findById(id);
            if (logToDeleteOpt.isEmpty()) {
                throw new RepositoryAccessException("Zapis s ID " + id + " ne postoji.");
            }
            CommunicationLog logToDelete = logToDeleteOpt.get();

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RepositoryAccessException("Brisanje nije uspjelo, zapis nije pronađen.");
            }

            // Dodaj ChangeLog nakon uspješnog brisanja
            String description = logToDelete.getLogDetails();
            ChangeLog changeLog = new ChangeLog(
                    UserSession.getLoggedInUser().getUsername(),
                    UserSession.getLoggedInUser().getRole(),
                    description,
                    null
            );
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> ChangeManager.addNewChange(changeLog));
            executor.shutdown();

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            accessManager.releaseAccess();
        }
    }

    public void update(CommunicationLog log) throws RepositoryAccessException, IOException {
        String sql = """
        UPDATE communication_log
        SET client_id = ?, user_id = ?, type = ?, date_time = ?, success = ?, note = ?
        WHERE id = ?
    """;
        accessManager.acquireAccess();
        try (Connection conn = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Dohvati prethodni zapis
            Optional<CommunicationLog> oldLogOpt = findById(log.getId());
            CommunicationLog oldLog = oldLogOpt.orElse(null);

            stmt.setLong(1, log.getClient().getId());
            stmt.setLong(2, log.getUserResponsible().getId());
            stmt.setString(3, log.getType().name());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getDateTime()));
            stmt.setBoolean(5, log.isSuccess());
            stmt.setString(6, log.getNote());
            stmt.setLong(7, log.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RepositoryAccessException("Update nije uspio, zapis nije pronađen.");
            }

            // Priprema dva odvojena opisa
            String oldDescription = oldLog != null ? oldLog.getLogDetails() : "Nepoznat prethodni zapis.";
            String newDescription = log.getLogDetails();

            ChangeLog logEntry = new ChangeLog(
                    UserSession.getLoggedInUser().getUsername(),
                    UserSession.getLoggedInUser().getRole(),
                    oldDescription,
                    newDescription
            );
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> ChangeManager.addNewChange(logEntry));
            executor.shutdown();

        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            accessManager.releaseAccess();
        }
    }


    public Optional<CommunicationLog> findById(Long id) throws RepositoryAccessException {
        String sql = """
            SELECT id, client_id, user_id, type, date_time, success, note
            FROM communication_log
            WHERE id = ?
            """;
        try (Connection conn = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CommunicationLog log = mapRowToLog(rs);
                    return Optional.of(log);
                } else {
                    return Optional.empty();
                }
            }
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }


}
