package service;

import domain.Client;
import domain.Session;
import filter.IFilter;
import repository.IRepository;
import repository.RepositoryException;
import validator.IValidator;
import validator.ValidationException;
import java.util.*;

public class SessionService {
    private IRepository<Integer, Client> clientRepository;
    private IRepository<Integer, Session> sessionRepository;
    private IValidator<Session> sessionValidator;

    public SessionService(IRepository<Integer, Session> sessionRepository, IRepository<Integer, Client> clientRepository, IValidator<Session> validator) {
        this.sessionRepository = sessionRepository;
        this.clientRepository = clientRepository;
        this.sessionValidator = validator;
    }

    public void addSession(Integer id, Integer clientId, String date, String time, String workoutDescription) throws RepositoryException, ValidationException {
        Session session = new Session(id, clientId, date, time, workoutDescription);
        sessionValidator.validate(session);

        if (clientRepository.getByID(clientId) == null)
            throw new RepositoryException("Client does not exist! Cannot create session!");

        sessionRepository.add(session);
    }

    public void deleteSession(Integer id) throws RepositoryException {
        sessionRepository.delete(id);
    }

    public void updateSession(Integer id, Integer clientId, String date, String time, String workoutDescription) throws RepositoryException, ValidationException {
        Session session = new Session(id, clientId, date, time, workoutDescription);
        sessionValidator.validate(session);

        if (clientRepository.getByID(clientId) == null)
            throw new RepositoryException("Client does not exist! Cannot update session!");

        sessionRepository.update(session);
    }

    public Session getSessionByID(Integer id) {
        return sessionRepository.getByID(id);
    }

    public ArrayList<Session> getAllSessions() {
        return sessionRepository.getAll();
    }

    public ArrayList<Session> filter(IFilter<Session> sessionFilter) {
        ArrayList<Session> filtered = new ArrayList<>();
        for (Session session : sessionRepository.getAll()) {
            if (sessionFilter.accept(session)) {
                filtered.add(session);
            }
        }
        return filtered;
    }

    // --- REPORTS ---

    public Client getMostActiveClient() {
        ArrayList<Session> allSessions = sessionRepository.getAll();
        if (allSessions.isEmpty()) return null;

        Map<Integer, Integer> sessionCounts = new HashMap<>();
        for (Session s : allSessions) {
            int clientId = s.getClientID();
            sessionCounts.put(clientId, sessionCounts.getOrDefault(clientId, 0) + 1);
        }

        Integer maxClientId = null;
        int maxCount = -1;

        for (Map.Entry<Integer, Integer> entry : sessionCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxClientId = entry.getKey();
            }
        }

        if (maxClientId != null) {
            return clientRepository.getByID(maxClientId);
        }
        return null;
    }


    public String getBusiestDay() {
        ArrayList<Session> allSessions = sessionRepository.getAll();
        if (allSessions.isEmpty()) return "No sessions data.";

        Map<String, Integer> dateCounts = new HashMap<>();
        for (Session s : allSessions) {
            String date = s.getDate();
            dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);
        }

        String bestDate = null;
        int maxCount = -1;

        for (Map.Entry<String, Integer> entry : dateCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestDate = entry.getKey();
            }
        }

        return bestDate + " with " + maxCount + " session(s)";
    }


    public Map<String, Integer> getWorkoutPopularity() {
        Map<String, Integer> popularityMap = new HashMap<>();

        for (Session s : sessionRepository.getAll()) {
            String type = s.getWorkoutDescription().toLowerCase();
            popularityMap.put(type, popularityMap.getOrDefault(type, 0) + 1);
        }

        return popularityMap;
    }


    public ArrayList<Client> getInactiveClients() {
        ArrayList<Client> inactiveClients = new ArrayList<>();
        ArrayList<Session> allSessions = sessionRepository.getAll();
        ArrayList<Client> allClients = clientRepository.getAll();

        Set<Integer> activeClientIds = new HashSet<>();
        for (Session s : allSessions) {
            activeClientIds.add(s.getClientID());
        }

        for (Client c : allClients) {
            if (!activeClientIds.contains(c.getID())) {
                inactiveClients.add(c);
            }
        }

        return inactiveClients;
    }


    public Map<String, Integer> getSessionsPerMonth() {
        Map<String, Integer> monthCounts = new HashMap<>();

        for (Session s : sessionRepository.getAll()) {
            String date = s.getDate();
            String[] parts = date.split("\\.");

            if (parts.length >= 2) {
                String month = parts[1];
                monthCounts.put(month, monthCounts.getOrDefault(month, 0) + 1);
            }
        }

        return monthCounts;
    }
}