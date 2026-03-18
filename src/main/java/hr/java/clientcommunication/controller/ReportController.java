package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.*;
import hr.java.clientcommunication.repository.CommunicationLogDatabaseRepository;
import hr.java.clientcommunication.utils.ChangeManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportController {

    @FXML
    private Label lblMostActiveUser;

    @FXML
    private Label lblMostSuccessfulUser;

    @FXML
    private Label lblTopClientSuccess;

    @FXML
    private Label lblTodayLogCount;

    private Timeline timeline;
    private final CommunicationLogDatabaseRepository commLogRepo = new CommunicationLogDatabaseRepository();

    public void initialize() {
        startRefreshing();
    }

    private void startRefreshing() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> refreshReport()),
                new KeyFrame(Duration.seconds(5))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshReport() {
        List<CommunicationLog> logs = loadLogs();

        ReportingTool<User, Long> mostActiveUser = getMostActiveUser(logs);
        ReportingTool<User, Long> mostSuccessfulUser = getMostSuccessfulUser(logs);
        ReportingTool<Client, Long> topClient = getTopClientBySuccessfulLogs(logs);
        TimedGroupedCount<String> todayLogsSummary = getTodayCommunicationLogsCount(logs);


        lblMostActiveUser.setText(mostActiveUser != null
                ? "Najaktivniji korisnik: " + mostActiveUser.getKey().getUsername() + " (" + mostActiveUser.getValue() + " komunikacija)"
                : "Nema podataka o korisnicima.");

        lblMostSuccessfulUser.setText(mostSuccessfulUser != null
                ? "Korisnik s najviše uspješnih komunikacija: " + mostSuccessfulUser.getKey().getUsername() + " (" + mostSuccessfulUser.getValue() + " uspješnih)"
                : "Nema podataka o uspješnim korisnicima.");

        lblTopClientSuccess.setText(topClient != null
                ? "Klijent s najviše uspješnih komunikacija: " + topClient.getKey().getFullName() + " (" + topClient.getValue() + " uspješnih)"
                : "Nema podataka o klijentima.");

        lblTodayLogCount.setText(todayLogsSummary.getGroupBy() + ": " + todayLogsSummary.getCount()
                + " (vrijeme: " + todayLogsSummary.getTimestamp() + ")");
    }


    private List<CommunicationLog> loadLogs() {
        return commLogRepo.findAll();
    }

    private ReportingTool<User, Long> getMostActiveUser(List<CommunicationLog> logs) {
        return logs.stream()
                .collect(Collectors.groupingBy(CommunicationLog::getUserResponsible, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new ReportingTool<>(1L, e.getKey(), e.getValue()))
                .orElse(null);
    }

    private ReportingTool<User, Long> getMostSuccessfulUser(List<CommunicationLog> logs) {
        return logs.stream()
                .filter(CommunicationLog::isSuccess)
                .collect(Collectors.groupingBy(CommunicationLog::getUserResponsible, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new ReportingTool<>(1L, e.getKey(), e.getValue()))
                .orElse(null);
    }

    private ReportingTool<Client, Long> getTopClientBySuccessfulLogs(List<CommunicationLog> logs) {
        Map<Long, List<CommunicationLog>> groupedByClientId = logs.stream()
                .filter(CommunicationLog::isSuccess)
                .collect(Collectors.groupingBy(log -> {
                    Client c = log.getClient();
                    return c != null ? c.getId() : -1L;
                }));

        Map.Entry<Long, List<CommunicationLog>> maxEntry = groupedByClientId.entrySet().stream()
                .max(Comparator.comparingLong(e -> e.getValue().size()))
                .orElse(null);

        if (maxEntry != null) {
            Client client = maxEntry.getValue().get(0).getClient();
            Long successfulCount = (long) maxEntry.getValue().size();
            return new ReportingTool<>(client, successfulCount);
        }

        return null;
    }

    private TimedGroupedCount<String> getTodayCommunicationLogsCount(List<CommunicationLog> allLogs) {
        LocalDate today = LocalDate.now();

        long todayLogCount = allLogs.stream()
                .filter(log -> log.getDateTime() != null && log.getDateTime().toLocalDate().equals(today))
                .count();

        String label = "Broj komunikacija danas";

        return new TimedGroupedCount<>(label, todayLogCount, java.time.LocalDateTime.now());
    }
    public void stopRefreshing() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
