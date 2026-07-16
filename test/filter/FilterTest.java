package filter;

import domain.Client;
import domain.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    private static Client client;
    private static Session session;

    @BeforeAll
    static void setUp() {
        client = new Client(10, "Ioan", "Popescu", "ioan@pop.com", "0712345678");
        session = new Session(20, 10, "20.10.2025", "14:30", "Chest & Triceps");
    }

    @Test
    void testFilterByID() {
        IFilter<Client> filterTrue = new FilterByID<>(10);
        IFilter<Client> filterFalse = new FilterByID<>(99);

        assertTrue(filterTrue.accept(client));
        assertFalse(filterFalse.accept(client));
    }

    @Test
    void testFilterByFirstname() {
        IFilter<Client> filterTrue = new FilterByFirstname("ioan");
        IFilter<Client> filterPartial = new FilterByFirstname("oa");
        IFilter<Client> filterFalse = new FilterByFirstname("Andrei");

        assertTrue(filterTrue.accept(client));
        assertTrue(filterPartial.accept(client));
        assertFalse(filterFalse.accept(client));
    }

    @Test
    void testFilterByLastname() {
        IFilter<Client> filterTrue = new FilterByLastname("popescu");
        IFilter<Client> filterPartial = new FilterByLastname("Pop");
        IFilter<Client> filterFalse = new FilterByLastname("Smith");

        assertTrue(filterTrue.accept(client));
        assertTrue(filterPartial.accept(client));
        assertFalse(filterFalse.accept(client));
    }

    @Test
    void testFilterByEmail() {
        IFilter<Client> filterTrue = new FilterByEmail("ioan@pop.com");
        IFilter<Client> filterPartial = new FilterByEmail("@pop");
        IFilter<Client> filterFalse = new FilterByEmail("test@gmail.com");

        assertTrue(filterTrue.accept(client));
        assertTrue(filterPartial.accept(client));
        assertFalse(filterFalse.accept(client));
    }

    @Test
    void testFilterByPhone() {
        IFilter<Client> filterTrue = new FilterByPhone("0712345678");
        IFilter<Client> filterPartial = new FilterByPhone("345");
        IFilter<Client> filterFalse = new FilterByPhone("999");

        assertTrue(filterTrue.accept(client));
        assertTrue(filterPartial.accept(client));
        assertFalse(filterFalse.accept(client));
    }


    @Test
    void testFilterSessionByID() {
        IFilter<Session> filterTrue = new FilterByID<>(20);
        IFilter<Session> filterFalse = new FilterByID<>(99);

        assertTrue(filterTrue.accept(session));
        assertFalse(filterFalse.accept(session));
    }

    @Test
    void testFilterByClientID() {
        IFilter<Session> filterTrue = new FilterByClientID(10);
        IFilter<Session> filterFalse = new FilterByClientID(99);

        assertTrue(filterTrue.accept(session));
        assertFalse(filterFalse.accept(session));
    }

    @Test
    void testFilterByDate() {
        IFilter<Session> filterTrue = new FilterByDate("20.10.2025");
        IFilter<Session> filterPartial = new FilterByDate(".10."); // Changed
        IFilter<Session> filterFalse = new FilterByDate("2026");

        assertTrue(filterTrue.accept(session));
        assertTrue(filterPartial.accept(session));
        assertFalse(filterFalse.accept(session));
    }

    @Test
    void testFilterByTime() {
        IFilter<Session> filterTrue = new FilterByTime("14:30");
        IFilter<Session> filterPartial = new FilterByTime(":30");
        IFilter<Session> filterFalse = new FilterByTime("09:00");

        assertTrue(filterTrue.accept(session));
        assertTrue(filterPartial.accept(session));
        assertFalse(filterFalse.accept(session));
    }

    @Test
    void testFilterByWorkout() {
        IFilter<Session> filterTrue = new FilterByWorkout("Chest & Triceps");
        IFilter<Session> filterPartial = new FilterByWorkout("chest");
        IFilter<Session> filterFalse = new FilterByWorkout("Legs");

        assertTrue(filterTrue.accept(session));
        assertTrue(filterPartial.accept(session));
        assertFalse(filterFalse.accept(session));
    }
}