package service;

import domain.Client;
import filter.FilterByFirstname;
import filter.IFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.FilteredRepository;
import repository.RepositoryException;
import validator.ClientValidator;
import validator.IValidator;
import validator.ValidationException;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

    private ClientService clientService;
    private FilteredRepository<Integer, Client> clientRepo;
    private IValidator<Client> clientValidator; // Added

    @BeforeEach
    void setUp() {
        clientRepo = new FilteredRepository<>();
        clientValidator = new ClientValidator(); // Added
        clientService = new ClientService(clientRepo, clientValidator); // Updated constructor
    }

    @Test
    void testAddClientSuccessful() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");

        assertEquals(1, clientRepo.getAll().size());
        assertEquals("Ioan", clientRepo.getByID(1).getFirstname());
    }

    @Test
    void testAddClientFails_DuplicateID() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");

        assertThrows(RepositoryException.class, () -> {
            clientService.addClient(1, "Andrei", "Mihai", "andrei@mihai.com", "0787654321");
        });
    }

    @Test
    void testAddClientFails_ValidationException() {
        // Test invalid phone number
        Exception e = assertThrows(ValidationException.class, () -> {
            clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "123");
        });
        assertTrue(e.getMessage().contains("Phone number must be exactly 10 digits"));

        // Test empty name
        e = assertThrows(ValidationException.class, () -> {
            clientService.addClient(2, "", "Pop", "ioan@pop.com", "0712345678");
        });
        assertTrue(e.getMessage().contains("First name cannot be empty"));
    }


    @Test
    void testDeleteClientSuccessful() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");
        assertEquals(1, clientService.getAllClients().size());

        clientService.deleteClient(1);

        assertEquals(0, clientService.getAllClients().size());
    }

    @Test
    void testDeleteClientFails_NotFound() {
        assertThrows(RepositoryException.class, () -> {
            clientService.deleteClient(99);
        });
    }

    @Test
    void testUpdateClientSuccessful() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");

        clientService.updateClient(1, "Ioan-Updated", "Pop-Updated", "ioan@upd.com", "0700000000");

        Client updatedClient = clientRepo.getByID(1);
        assertEquals("Ioan-Updated", updatedClient.getFirstname());
        assertEquals("Pop-Updated", updatedClient.getLastname());
    }

    @Test
    void testUpdateClientFails_NotFound() {
        assertThrows(RepositoryException.class, () -> {
            clientService.updateClient(99, "Test", "Test", "test@test.com", "0711111111");
        });
    }

    @Test
    void testUpdateClientFails_ValidationException() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");

        // Test invalid email
        Exception e = assertThrows(ValidationException.class, () -> {
            clientService.updateClient(1, "Ioan", "Pop", "bad-email", "0712345678");
        });
        assertTrue(e.getMessage().contains("Email is not valid"));
    }

    @Test
    void testGetAllClients() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");
        clientService.addClient(2, "Andrei", "Mihai", "andrei@mihai.com", "0787654321");

        ArrayList<Client> allClients = clientService.getAllClients();

        assertEquals(2, allClients.size());
    }

    @Test
    void testGetAllClients_Empty() {
        ArrayList<Client> allClients = clientService.getAllClients();
        assertEquals(0, allClients.size());
    }

    @Test
    void testFilter() throws RepositoryException, ValidationException {
        clientService.addClient(1, "Ioan", "Pop", "ioan@pop.com", "0712345678");
        clientService.addClient(2, "Andrei", "Mihai", "andrei@mihai.com", "0787654321");

        IFilter<Client> filter = new FilterByFirstname("Ioan");
        ArrayList<Client> result = clientService.filter(filter);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getID());
    }
}