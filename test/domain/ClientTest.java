package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void testConstructorAndGetters() {
        Client client = new Client(1, "Ioan", "Pop", "ioan@pop.com", "07123");

        // Use assertEquals to check if getters return the correct values
        assertEquals(1, client.getID());
        assertEquals("Ioan", client.getFirstname());
        assertEquals("Pop", client.getLastname());
        assertEquals("ioan@pop.com", client.getEmail());
        assertEquals("07123", client.getPhone());
    }

    @Test
    void testSetters() {
        Client client = new Client(1, "Ioan", "Pop", "ioan@pop.com", "07123");

        // Use setters to change values
        client.setID(2);
        client.setFirstname("Andrei");
        client.setLastname("Mihai");
        client.setEmail("andrei@mihai.com");
        client.setPhone("07456");

        // Check if getters return the new values
        assertEquals(2, client.getID());
        assertEquals("Andrei", client.getFirstname());
        assertEquals("Mihai", client.getLastname());
        assertEquals("andrei@mihai.com", client.getEmail());
        assertEquals("07456", client.getPhone());
    }

    @Test
    void testEquals() {
        Client client1 = new Client(1, "Ioan", "Pop", "ioan@pop.com", "07123");
        Client client2 = new Client(1, "Ioana", "Popa", "ioana@popa.com", "07777");
        Client client3 = new Client(2, "Ioan", "Pop", "ioan@pop.com", "07123");

        // Test for equality based on ID
        assertTrue(client1.equals(client1)); // Same object
        assertTrue(client1.equals(client2)); // Different objects, same ID
        assertFalse(client1.equals(client3)); // Different IDs
        assertFalse(client1.equals(null)); // Test against null
        assertFalse(client1.equals("a string")); // Test against different class
    }

    @Test
    void testHashCode() {
        Client client1 = new Client(1, "Ioan", "Pop", "ioan@pop.com", "07123");
        Client client2 = new Client(1, "Ioana", "Popa", "ioana@popa.com", "07777");

        // Objects with the same ID should have the same hashcode
        assertEquals(client1.hashCode(), client2.hashCode());
    }

    @Test
    void testToString() {
        Client client = new Client(1, "Ioan", "Pop", "ioan@pop.com", "07123");
        String clientString = client.toString();

        // Check if the toString() output contains the key information
        assertTrue(clientString.contains("Client{"));
        assertTrue(clientString.contains("ID: 1"));
        assertTrue(clientString.contains("Fistname: Ioan"));
        assertTrue(clientString.contains("Lastname: Pop"));
        assertTrue(clientString.contains("Email: ioan@pop.com"));
        assertTrue(clientString.contains("Phone: 07123"));
    }
}