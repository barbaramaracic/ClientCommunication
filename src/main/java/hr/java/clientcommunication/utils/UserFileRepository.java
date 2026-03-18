package hr.java.clientcommunication.utils;

import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.exception.UserFileNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserFileRepository {
    private static final String USERS_TXT_PATH = "dat/users.txt";

    public void updateUserInFile(User user) throws IOException {
        Path path = Paths.get(USERS_TXT_PATH);
        List<String> lines = Files.readAllLines(path);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String updatedLine = String.format("%d;%s;%s;%s;%s;%s;%s",
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate().format(formatter),
                user.getUsername(),
                user.getHashpassword(),
                user.getRole().name()
        );

        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(user.getId() + ";")) {
                updatedLines.add(updatedLine);
            } else {
                updatedLines.add(line);
            }
        }

        Files.write(path, updatedLines);
    }

    public void deleteUserFromFile(Long id) throws IOException {
        Path path = Paths.get(USERS_TXT_PATH);
        List<String> lines = Files.readAllLines(path);

        List<String> filteredLines = new ArrayList<>();
        for (String line : lines) {
            if (!line.startsWith(id + ";")) {
                filteredLines.add(line);
            }
        }

        Files.write(path, filteredLines);
    }

    public List<User> findAllFromFile() throws IOException, UserFileNotFoundException  {
        List<User> users = new ArrayList<>();
        Path path = Paths.get(USERS_TXT_PATH);

        if (!Files.exists(path)) {
            throw new UserFileNotFoundException("User File does not exist.");
        }

        List<String> lines = Files.readAllLines(path);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String line : lines) {
            if (line.isBlank()) continue;

            String[] parts = line.split(";");
            if (parts.length != 7) continue;

            Long id = Long.parseLong(parts[0]);
            String firstName = parts[1];
            String lastName = parts[2];
            LocalDate birthDate = LocalDate.parse(parts[3], formatter);
            String username = parts[4];
            String hashpassword = parts[5];
            UserRole role = UserRole.valueOf(parts[6]);

            User user = new User(id, firstName, lastName, birthDate, username, hashpassword, role);
            users.add(user);
        }

        return users;
    }

    public Optional<User> findByIdFromFile(Long id) throws IOException {
        Path path = Paths.get(USERS_TXT_PATH);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        List<String> lines = Files.readAllLines(path);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String line : lines) {
            if (line.isBlank()) continue;

            String[] parts = line.split(";");
            if (parts.length != 7) continue;

            Long parsedId = Long.parseLong(parts[0]);
            if (parsedId.equals(id)) {
                String firstName = parts[1];
                String lastName = parts[2];
                LocalDate birthDate = LocalDate.parse(parts[3], formatter);
                String username = parts[4];
                String hashpassword = parts[5];
                UserRole role = UserRole.valueOf(parts[6]);

                User user = new User(parsedId, firstName, lastName, birthDate, username, hashpassword, role);
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

}
