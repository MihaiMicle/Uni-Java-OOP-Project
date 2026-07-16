package repository;

import domain.Client;
import filter.FilterByFirstname;
import filter.IFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ClientRepositoryTest {

    private FilteredRepository<Integer, Client> repo;

    @BeforeEach // This method runs before each @Test
    void setUp() {
        // Create a new, empty repository for every test
        repo = new FilteredRepository<>();
    }

    @Test
    void testAddSuccessful() throws RepositoryException {
        Client client = new Client(1, "Ioan", "Pop", "e", "p");
        repo.add(client);

        assertEquals(1, repo.getAll().size());
        assertEquals(client, repo.getByID(1));
    }

    @Test
    void testAddFails_DuplicateID() throws RepositoryException {
        Client client1 = new Client(1, "Ioan", "Pop", "e", "p");
        repo.add(client1);

        Client client2_sameID = new Client(1, "Andrei", "Mihai", "e2", "p2");

        // Check that adding a client with a duplicate ID throws the correct exception
        Exception e = assertThrows(RepositoryException.class, () -> {
            repo.add(client2_sameID);
        });

        assertEquals("The ID: 1 already exists!", e.getMessage());
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void testDeleteSuccessful() throws RepositoryException {
        Client client = new Client(1, "Ioan", "Pop", "e", "p");
        repo.add(client);

        repo.delete(1);

        assertEquals(0, repo.getAll().size());
        assertNull(repo.getByID(1));
    }

    @Test
    void testDeleteFails_NotFound() {
        // Check that deleting a non-existent ID throws an exception
        Exception e = assertThrows(RepositoryException.class, () -> {
            repo.delete(99); // 99 does not exist
        });
        assertEquals("The ID: 99 does not exists!", e.getMessage());
    }

    @Test
    void testUpdateSuccessful() throws RepositoryException {
        Client originalClient = new Client(1, "Ioan", "Pop", "e", "p");
        repo.add(originalClient);

        Client updatedClient = new Client(1, "Ioan-Updated", "Pop-Updated", "e-u", "p-u");
        repo.update(updatedClient);

        assertEquals(1, repo.getAll().size());
        assertEquals("Ioan-Updated", repo.getByID(1).getFirstname());
        assertEquals("Pop-Updated", repo.getByID(1).getLastname());
    }

    @Test
    void testUpdateFails_NotFound() {
        Client client = new Client(99, "Test", "Test", "e", "p");

        Exception e = assertThrows(RepositoryException.class, () -> {
            repo.update(client); // ID 99 does not exist in the repo
        });
        assertEquals("The ID: 99 does not exists!", e.getMessage());
    }

    @Test
    void testGetAll() throws RepositoryException {
        Client client1 = new Client(1, "Ioan", "Pop", "e", "p");
        Client client2 = new Client(2, "Andrei", "Mihai", "e2", "p2");
        repo.add(client1);
        repo.add(client2);

        ArrayList<Client> allClients = repo.getAll();

        assertEquals(2, allClients.size());
        assertTrue(allClients.contains(client1));
        assertTrue(allClients.contains(client2));
    }

    @Test
    void testGetAll_Empty() {
        ArrayList<Client> allClients = repo.getAll();
        assertEquals(0, allClients.size());
    }

    @Test
    void testGetByID_Found() throws RepositoryException {
        Client client1 = new Client(1, "Ioan", "Pop", "e", "p");
        repo.add(client1);
        assertEquals(client1, repo.getByID(1));
    }

    @Test
    void testGetByID_NotFound() {
        assertNull(repo.getByID(99)); // Nothing in repo, should return null
    }

    @Test
    void testFilter() throws RepositoryException {
        Client client1 = new Client(1, "Ioan", "Pop", "e", "p");
        Client client2 = new Client(2, "Andrei", "Mihai", "e2", "p2");
        Client client3 = new Client(3, "Ioana", "Popescu", "e3", "p3");
        repo.add(client1);
        repo.add(client2);
        repo.add(client3);

        IFilter<Client> filter = new FilterByFirstname("Ioan");
        ArrayList<Client> result = repo.filter(filter);

        assertEquals(2, result.size());
        assertTrue(result.contains(client1));
        assertTrue(result.contains(client3));
        assertFalse(result.contains(client2));
    }

    @Test
    void testFilter_NoResults() throws RepositoryException {
        Client client1 = new Client(1, "Ioan", "Pop", "e", "p");
        repo.add(client1);

        IFilter<Client> filter = new FilterByFirstname("Zzz");
        ArrayList<Client> result = repo.filter(filter);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }
}