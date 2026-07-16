package service;

import domain.Client;
import domain.Session;
import filter.FilterByWorkout;
import filter.IFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.FilteredRepository;
import repository.RepositoryException;
import validator.IValidator;
import validator.SessionValidator;
import validator.ValidationException;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    private SessionService sessionService;
    private FilteredRepository<Integer, Session> sessionRepo;
    private FilteredRepository<Integer, Client> clientRepo;
    private IValidator<Session> sessionValidator;

    @BeforeEach
    void setUp() {
        sessionRepo = new FilteredRepository<>();
        clientRepo = new FilteredRepository<>();
        sessionValidator = new SessionValidator();
        sessionService = new SessionService(sessionRepo, clientRepo, sessionValidator);

        try {
            clientRepo.add(new Client(1, "Test", "Client", "test@client.com", "0712345678"));
        } catch (RepositoryException e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    void testAddSessionSuccessful() throws RepositoryException, ValidationException { // Added ValidationException
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");

        assertEquals(1, sessionRepo.getAll().size());
        assertEquals("Chest", sessionRepo.getByID(10).getWorkoutDescription());
    }

    @Test
    void testAddSessionFails_ClientNotFound() {
        // The service logic I provided throws RepositoryException, not RuntimeException
        Exception e = assertThrows(RepositoryException.class, () -> {
            sessionService.addSession(10, 99, "01.01.2025", "12:00", "Chest");
        });
        assertEquals("Client does not exist! Cannot create session!", e.getMessage());
        assertEquals(0, sessionRepo.getAll().size());
    }

    @Test
    void testAddSessionFails_ValidationException() {
        // Test invalid date format
        Exception e = assertThrows(ValidationException.class, () -> {
            sessionService.addSession(10, 1, "01-01-2025", "12:00", "Chest"); // Bad date format
        });
        assertTrue(e.getMessage().contains("Date format must be dd.mm.yyyy"));

        // Test invalid time format
        e = assertThrows(ValidationException.class, () -> {
            sessionService.addSession(11, 1, "01.01.2025", "25:00", "Chest"); // Bad time
        });
        assertTrue(e.getMessage().contains("Time format must be hh:mm"));

        // Test empty workout description
        e = assertThrows(ValidationException.class, () -> {
            sessionService.addSession(12, 1, "01.01.2025", "12:00", " "); // Empty workout
        });
        assertTrue(e.getMessage().contains("Workout description cannot be empty"));
    }


    @Test
    void testAddSessionFails_DuplicateSessionID() throws RepositoryException, ValidationException { // Added ValidationException
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");

        assertThrows(RepositoryException.class, () -> {
            sessionService.addSession(10, 1, "02.02.2025", "13:00", "Back");
        });
    }

    @Test
    void testDeleteSessionSuccessful() throws RepositoryException, ValidationException { // Added ValidationException
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");
        assertEquals(1, sessionService.getAllSessions().size());

        sessionService.deleteSession(10);

        assertEquals(0, sessionService.getAllSessions().size());
    }

    @Test
    void testDeleteSessionFails_NotFound() {
        assertThrows(RepositoryException.class, () -> {
            sessionService.deleteSession(99);
        });
    }

    @Test
    void testUpdateSessionSuccessful() throws RepositoryException, ValidationException { // Added ValidationException
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");

        sessionService.updateSession(10, 1, "11.11.2025", "14:00", "Legs");

        Session updated = sessionRepo.getByID(10);
        assertEquals("11.11.2025", updated.getDate());
        assertEquals("Legs", updated.getWorkoutDescription());
    }

    @Test
    void testUpdateSessionFails_ClientNotFound() {
        // The service logic I provided throws RepositoryException, not RuntimeException
        Exception e = assertThrows(RepositoryException.class, () -> {
            sessionService.updateSession(10, 99, "01.01.2025", "12:00", "Chest");
        });
        assertEquals("Client does not exist! Cannot update session!", e.getMessage());
    }

    @Test
    void testUpdateSessionFails_ValidationException() throws RepositoryException, ValidationException {
        // Add a valid session first
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");

        // Try to update with invalid data
        Exception e = assertThrows(ValidationException.class, () -> {
            sessionService.updateSession(10, 1, "bad-date", "12:00", "Chest"); // Invalid date
        });
        assertTrue(e.getMessage().contains("Date format must be dd.mm.yyyy"));
    }


    @Test
    void testUpdateSessionFails_SessionNotFound() {
        // No validation exception here, as it fails on repository check first
        assertThrows(RepositoryException.class, () -> {
            sessionService.updateSession(99, 1, "01.01.2025", "12:00", "Chest");
        });
    }

    @Test
    void testGetSessionByID() throws RepositoryException, ValidationException {
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");

        Session found = sessionService.getSessionByID(10);
        Session notFound = sessionService.getSessionByID(99);

        assertNotNull(found);
        assertEquals("Chest", found.getWorkoutDescription());
        assertNull(notFound);
    }

    @Test
    void testGetAllSessions() throws RepositoryException, ValidationException {
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest");
        sessionService.addSession(11, 1, "02.02.2025", "13:00", "Back");

        ArrayList<Session> all = sessionService.getAllSessions();

        assertEquals(2, all.size());
    }

    @Test
    void testFilter() throws RepositoryException, ValidationException {
        sessionService.addSession(10, 1, "01.01.2025", "12:00", "Chest & Triceps");
        sessionService.addSession(11, 1, "02.02.2025", "13:00", "Back & Biceps");

        IFilter<Session> filter = new FilterByWorkout("Chest");
        ArrayList<Session> result = sessionService.filter(filter);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getID());
    }
}

