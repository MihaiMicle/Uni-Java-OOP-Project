package repository;

import domain.Session;
import filter.FilterByWorkout;
import filter.IFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SessionRepositoryTest {

    private FilteredRepository<Integer, Session> repo;

    @BeforeEach
    void setUp() {
        repo = new FilteredRepository<>();
    }

    @Test
    void testAddSuccessful() throws RepositoryException {
        Session session = new Session(1, 1, "01.01.2025", "10:00", "Chest");
        repo.add(session);

        assertEquals(1, repo.getAll().size());
        assertEquals(session, repo.getByID(1));
    }

    @Test
    void testAddFails_DuplicateID() throws RepositoryException {
        Session session1 = new Session(1, 1, "01.01.2025", "10:00", "Chest");
        repo.add(session1);

        Session session2_sameID = new Session(1, 2, "02.02.2025", "11:00", "Back");

        Exception e = assertThrows(RepositoryException.class, () -> {
            repo.add(session2_sameID);
        });

        assertEquals("The ID: 1 already exists!", e.getMessage());
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void testDeleteSuccessful() throws RepositoryException {
        Session session = new Session(1, 1, "01.01.2025", "10:00", "Chest");
        repo.add(session);

        repo.delete(1);

        assertEquals(0, repo.getAll().size());
        assertNull(repo.getByID(1));
    }

    @Test
    void testDeleteFails_NotFound() {
        Exception e = assertThrows(RepositoryException.class, () -> {
            repo.delete(99);
        });
        assertEquals("The ID: 99 does not exists!", e.getMessage());
    }

    @Test
    void testUpdateSuccessful() throws RepositoryException {
        Session originalSession = new Session(1, 1, "01.01.2025", "10:00", "Chest");
        repo.add(originalSession);

        Session updatedSession = new Session(1, 1, "03.03.2025", "12:00", "Legs");
        repo.update(updatedSession);

        assertEquals(1, repo.getAll().size());
        assertEquals("Legs", repo.getByID(1).getWorkoutDescription());
        assertEquals("03.03.2025", repo.getByID(1).getDate());
    }

    @Test
    void testUpdateFails_NotFound() {
        Session session = new Session(99, 1, "01.01.2025", "10:00", "Chest");

        Exception e = assertThrows(RepositoryException.class, () -> {
            repo.update(session);
        });
        assertEquals("The ID: 99 does not exists!", e.getMessage());
    }

    @Test
    void testGetAll() throws RepositoryException {
        Session session1 = new Session(1, 1, "01.01.2025", "10:00", "Chest");
        Session session2 = new Session(2, 2, "02.02.2025", "11:00", "Back");
        repo.add(session1);
        repo.add(session2);

        ArrayList<Session> allSessions = repo.getAll();

        assertEquals(2, allSessions.size());
        assertTrue(allSessions.contains(session1));
        assertTrue(allSessions.contains(session2));
    }

    @Test
    void testGetAll_Empty() {
        ArrayList<Session> allSessions = repo.getAll();
        assertEquals(0, allSessions.size());
    }

    @Test
    void testGetByID_Found() throws RepositoryException {
        Session session1 = new Session(1, 1, "01.01.2025", "10:00", "Chest");
        repo.add(session1);
        assertEquals(session1, repo.getByID(1));
    }

    @Test
    void testGetByID_NotFound() {
        assertNull(repo.getByID(99));
    }

    @Test
    void testFilter() throws RepositoryException {
        Session session1 = new Session(1, 1, "01.01.2025", "10:00", "Chest Day");
        Session session2 = new Session(2, 2, "02.02.2025", "11:00", "Back Day");
        Session session3 = new Session(3, 1, "03.03.2025", "12:00", "Chest & Triceps");
        repo.add(session1);
        repo.add(session2);
        repo.add(session3);

        IFilter<Session> filter = new FilterByWorkout("Chest");
        ArrayList<Session> result = repo.filter(filter);

        assertEquals(2, result.size());
        assertTrue(result.contains(session1));
        assertTrue(result.contains(session3));
        assertFalse(result.contains(session2));
    }

    @Test
    void testFilter_NoResults() throws RepositoryException {
        Session session1 = new Session(1, 1, "01.01.2025", "10:00", "Chest Day");
        repo.add(session1);

        IFilter<Session> filter = new FilterByWorkout("Yoga");
        ArrayList<Session> result = repo.filter(filter);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }
}