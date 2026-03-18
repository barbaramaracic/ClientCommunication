package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.*;
import hr.java.clientcommunication.exception.EmptyRepositoryResultException;
import hr.java.clientcommunication.exception.RepositoryAccessException;
import hr.java.clientcommunication.record.ChangeLog;
import hr.java.clientcommunication.utils.ChangeManager;
import hr.java.clientcommunication.utils.DatabaseAccessThreadManager;
import hr.java.clientcommunication.utils.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientDatabaseRepository {

    private static final Logger log = LoggerFactory.getLogger(ClientDatabaseRepository.class);
    private final PersonDatabaseRepository personRepo = new PersonDatabaseRepository();
    private final AddressDatabaseRepository addressRepo = new AddressDatabaseRepository();
    private final CommunicationPreferencesDatabaseRepository preferenceRepo = new CommunicationPreferencesDatabaseRepository();
    private static final DatabaseAccessThreadManager accessManager = new DatabaseAccessThreadManager();
    private static final String ADDRESS_ID = "address_id";
    private static final String PREFERENCE_ID = "preference_id";

    public List<Client> findAll() throws RepositoryAccessException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, email, phone, address_id, preference_id FROM client";
        accessManager.acquireAccess();
        try (Connection connection = DatabaseManager.connectToDatabase();
             var stmt = connection.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getLong("id"));
                client.setEmail(rs.getString("email"));
                client.setPhone(rs.getString("phone"));

                client = (Client) personRepo.loadPersonData(client.getId(), client);
                if (client == null) {
                    continue;
                }

                client.setAddress(addressRepo.findById(rs.getLong(ADDRESS_ID)));
                client.setPreference(preferenceRepo.findById(rs.getLong(PREFERENCE_ID)));

                clients.add(client);
            }

            return clients;

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to find all clients", e);
        }
        catch (EmptyRepositoryResultException e) {
            log.error("Could not retrieve data");
            return clients;
        }
        finally {accessManager.releaseAccess();}
    }

    public void save(Client client) throws RepositoryAccessException {
        accessManager.acquireAccess();
        try (Connection connection = DatabaseManager.connectToDatabase()) {
            saveClientData(client, connection);
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to connect or save client", e);
        } finally {
            accessManager.releaseAccess();
        }
    }

    private void saveClientData(Client client, Connection connection) throws SQLException {
        try {
            connection.setAutoCommit(false);

            Long personId = personRepo.save(client);
            client.setId(personId);

            addressRepo.save(client.getAddress());
            Long addressId = client.getAddress().getId();

            Long preferenceId = preferenceRepo.save(client.getPreference(), connection);
            client.getPreference().setId(preferenceId);

            String sql = "INSERT INTO client (id, email, phone, address_id, preference_id) VALUES (?, ?, ?, ?, ?)";
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, client.getId());
                stmt.setString(2, client.getEmail());
                stmt.setString(3, client.getPhone());
                stmt.setLong(4, addressId);
                stmt.setLong(5, preferenceId);
                stmt.executeUpdate();
            }

            connection.commit();

            String description = String.format(client.toString());

            ChangeLog log = new ChangeLog(
                    UserSession.getLoggedInUser().getUsername(),
                    UserSession.getLoggedInUser().getRole(),
                    null,
                    description
            );
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> ChangeManager.addNewChange(log));
            executor.shutdown();

        } catch (Exception e) {
            connection.rollback();
            throw new RepositoryAccessException("Failed to save client with all dependencies", e);
        } finally {
            connection.setAutoCommit(true);
        }
    }


    public void deleteById(Long id) throws RepositoryAccessException {

        accessManager.acquireAccess();
            try (Connection connection = DatabaseManager.connectToDatabase()) {
                connection.setAutoCommit(false);
                Optional<Client> clientToDeleteOpt = findById(id);
                if (clientToDeleteOpt.isEmpty()) {
                    throw new RepositoryAccessException("Client with ID " + id + " does not exist.");
                }
                Client clientToDelete = clientToDeleteOpt.get();
                String description = String.format(clientToDelete.toString());
                ChangeLog log = new ChangeLog(
                        UserSession.getLoggedInUser().getUsername(),
                        UserSession.getLoggedInUser().getRole(),
                        description,
                        null
                );
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(() -> ChangeManager.addNewChange(log));
                executor.shutdown();
                deleteClientData(id, connection);
            } catch (IOException | SQLException e) {
                throw new RepositoryAccessException(e);
            }
            finally {
                accessManager.releaseAccess();
            }
    }

    private static void deleteClientData(Long id, Connection connection) throws SQLException {
        try (
                PreparedStatement refStmt = connection.prepareStatement(
                        "SELECT address_id, preference_id FROM client WHERE id = ?");
                PreparedStatement deleteClient = connection.prepareStatement(
                        "DELETE FROM client WHERE id = ?");
                PreparedStatement deletePerson = connection.prepareStatement(
                        "DELETE FROM person WHERE id = ?");
                PreparedStatement deleteAddress = connection.prepareStatement(
                        "DELETE FROM address WHERE id = ?");
                PreparedStatement deletePref = connection.prepareStatement(
                        "DELETE FROM communication_preference WHERE id = ?")
        ) {
            refStmt.setLong(1, id);
            Long addressId = null;
            Long preferenceId = null;

            try (ResultSet refRs = refStmt.executeQuery()) {
                if (!refRs.next()) {
                    throw new RepositoryAccessException("Client with ID " + id + " does not exist.");
                }
                addressId = refRs.getLong(ADDRESS_ID);
                if (refRs.wasNull()) {
                    addressId = null;
                }
                preferenceId = refRs.getLong(PREFERENCE_ID);
                if (refRs.wasNull()) {
                    preferenceId = null;
                }
            }

            deleteClient.setLong(1, id);
            deleteClient.executeUpdate();
            deletePerson.setLong(1, id);
            deletePerson.executeUpdate();
            if (addressId != null) {
                deleteAddress.setLong(1, addressId);
                deleteAddress.executeUpdate();
            }
            if (preferenceId != null) {
                deletePref.setLong(1, preferenceId);
                deletePref.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void update(Client client) throws RepositoryAccessException {
        accessManager.acquireAccess();
        try (Connection connection = DatabaseManager.connectToDatabase()) {
            connection.setAutoCommit(false);

            Optional<Client> oldClientOpt = findById(client.getId());
            if (oldClientOpt.isEmpty()) {
                throw new RepositoryAccessException("Client with ID " + client.getId() + " does not exist.");
            }
            Client oldClient = oldClientOpt.get();

            updateClientData(client, connection, oldClient);

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
        finally {
            accessManager.releaseAccess();
        }
    }

    private static void updateClientData(Client client, Connection connection, Client oldClient) throws SQLException {
        try (
                PreparedStatement personStmt = connection.prepareStatement(
                        "UPDATE person SET first_name = ?, last_name = ?, birth_date = ? WHERE id = ?");
                PreparedStatement clientStmt = connection.prepareStatement(
                        "UPDATE client SET email = ?, phone = ? WHERE id = ?");
                PreparedStatement addressStmt = connection.prepareStatement(
                        "UPDATE address SET street = ?, house_number = ?, city = ?, postal_code = ? " +
                                "WHERE id = (SELECT address_id FROM client WHERE id = ?)");
                PreparedStatement prefStmt = connection.prepareStatement(
                        "UPDATE communication_preference SET email_allowed = ?, sms_allowed = ?, phone_call_allowed = ? " +
                                "WHERE id = (SELECT preference_id FROM client WHERE id = ?)")
        ) {
            personStmt.setString(1, client.getFirstName());
            personStmt.setString(2, client.getLastName());
            personStmt.setDate(3, Date.valueOf(client.getBirthDate()));
            personStmt.setLong(4, client.getId());
            personStmt.executeUpdate();

            clientStmt.setString(1, client.getEmail());
            clientStmt.setString(2, client.getPhone());
            clientStmt.setLong(3, client.getId());
            clientStmt.executeUpdate();

            addressStmt.setString(1, client.getAddress().getStreet());
            addressStmt.setString(2, client.getAddress().getHouseNumber());
            addressStmt.setString(3, client.getAddress().getCity());
            addressStmt.setString(4, client.getAddress().getPostalCode());
            addressStmt.setLong(5, client.getId());
            addressStmt.executeUpdate();

            prefStmt.setBoolean(1, client.getPreference().isEmailAllowed());
            prefStmt.setBoolean(2, client.getPreference().isSmsAllowed());
            prefStmt.setBoolean(3, client.getPreference().isPhoneCallAllowed());
            prefStmt.setLong(4, client.getId());
            prefStmt.executeUpdate();

            connection.commit();

            String oldDescription = String.format(oldClient.toString());
            String newDescription = String.format(client.toString());

            ChangeLog log = new ChangeLog(
                    UserSession.getLoggedInUser().getUsername(),
                    UserSession.getLoggedInUser().getRole(),
                    oldDescription, // old value
                    newDescription  // new value
            );
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> ChangeManager.addNewChange(log));
            executor.shutdown();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Optional<Client> findById(Long id) throws RepositoryAccessException {
        try (Connection connection = DatabaseManager.connectToDatabase();
             var stmt = connection.prepareStatement(
                     "SELECT id, email, phone, address_id, preference_id FROM client WHERE id = ?")) {

            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getLong("id"));
                    client.setEmail(rs.getString("email"));
                    client.setPhone(rs.getString("phone"));

                    client = (Client) personRepo.loadPersonData(client.getId(), client);
                    if (client == null) {
                        return Optional.empty();
                    }

                    client.setAddress(addressRepo.findById(rs.getLong(ADDRESS_ID)));
                    client.setPreference(preferenceRepo.findById(rs.getLong(PREFERENCE_ID)));

                    return Optional.of(client);
                } else {
                    return Optional.empty();
                }
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException("Failed to find client", e);
        }
    }

}
