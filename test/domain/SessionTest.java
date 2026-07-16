package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void testConstructorAndGetters() {
        Session session = new Session(1, 10, "20.10.2025", "10:00", "Chest Day");

        assertEquals(1, session.getID());
        assertEquals(10, session.getClientID());
        assertEquals("20.10.2025", session.getDate());
        assertEquals("10:00", session.getTime());
        assertEquals("Chest Day", session.getWorkoutDescription());
    }

    @Test
    void testSetters() {
        Session session = new Session(1, 10, "20.10.2025", "10:00", "Chest Day");

        session.setID(2);
        session.setClientID(11);
        session.setDate("21.11.2025");
        session.setTime("11:30");
        session.setWorkoutDescription("Leg Day");

        assertEquals(2, session.getID());
        assertEquals(11, session.getClientID());
        assertEquals("21.11.2025", session.getDate());
        assertEquals("11:30", session.getTime());
        assertEquals("Leg Day", session.getWorkoutDescription());
    }

    @Test
    void testEquals() {
        Session session1 = new Session(1, 10, "20.10.2025", "10:00", "Chest Day");
        Session session2 = new Session(1, 11, "11.11.2025", "11:00", "Leg Day");
        Session session3 = new Session(2, 10, "20.10.2025", "10:00", "Chest Day");

        assertTrue(session1.equals(session1)); // Same object
        assertTrue(session1.equals(session2)); // Different objects, same ID
        assertFalse(session1.equals(session3)); // Different IDs
        assertFalse(session1.equals(null)); // Test against null
        assertFalse(session1.equals("a string")); // Test against different class
    }

    @Test
    void testHashCode() {
        Session session1 = new Session(1, 10, "20.10.2025", "10:00", "Chest Day");
        Session session2 = new Session(1, 11, "11.11.2025", "11:00", "Leg Day");

        // Objects with the same ID should have the same hashcode
        assertEquals(session1.hashCode(), session2.hashCode());
    }

    @Test
    void testToString() {
        Session session = new Session(1, 10, "20.10.2025", "10:00", "Chest Day");
        String sessionString = session.toString();

        assertTrue(sessionString.contains("Session{"));
        assertTrue(sessionString.contains("ID: 1"));
        assertTrue(sessionString.contains("Client ID: 10"));
        assertTrue(sessionString.contains("Date: 20.10.2025"));
        assertTrue(sessionString.contains("Time: 10:00"));
        assertTrue(sessionString.contains("Workout Description: Chest Day"));
    }
}