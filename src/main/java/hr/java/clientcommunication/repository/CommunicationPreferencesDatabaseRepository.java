package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.CommunicationPreference;
import hr.java.clientcommunication.utils.DatabaseManager;

import java.io.IOException;
import java.sql.*;

public class CommunicationPreferencesDatabaseRepository {

    public Long save(CommunicationPreference preference, Connection connection) throws SQLException {
        String sql = "INSERT INTO communication_preference (email_allowed, sms_allowed, phone_call_allowed) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, preference.isEmailAllowed());
            stmt.setBoolean(2, preference.isSmsAllowed());
            stmt.setBoolean(3, preference.isPhoneCallAllowed());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Failed to retrieve generated communication preference ID");
                }
            }
        }
    }

    public void update(Long preferenceId, CommunicationPreference preference, Connection connection) throws SQLException {
        String sql = "UPDATE communication_preference SET email_allowed = ?, sms_allowed = ?, phone_call_allowed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, preference.isEmailAllowed());
            stmt.setBoolean(2, preference.isSmsAllowed());
            stmt.setBoolean(3, preference.isPhoneCallAllowed());
            stmt.setLong(4, preferenceId);
            stmt.executeUpdate();
        }
    }

    public void delete(Long preferenceId, Connection connection) throws SQLException {
        String sql = "DELETE FROM communication_preference WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, preferenceId);
            stmt.executeUpdate();
        }
    }
    public CommunicationPreference findById(Long id) throws SQLException {
        String sql = "SELECT id, email_allowed, sms_allowed, phone_call_allowed FROM communication_preference WHERE id = ?";
        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CommunicationPreference preference = new CommunicationPreference();
                    preference.setId(rs.getLong("id"));
                    preference.setEmailAllowed(rs.getBoolean("email_allowed"));
                    preference.setSmsAllowed(rs.getBoolean("sms_allowed"));
                    preference.setPhoneCallAllowed(rs.getBoolean("phone_call_allowed"));
                    return preference;
                } else {
                    return null;
                }
            }
        } catch (SQLException | IOException e) {
            throw new SQLException("Failed to find CommunicationPreference with id " + id, e);
        }
    }

}
