package service;

import domain.Client;
import filter.IFilter;
import repository.IRepository;
import repository.RepositoryException;
import validator.IValidator;
import validator.ValidationException;

import java.util.ArrayList;

public class ClientService {
    private IRepository<Integer, Client> clientRepository;
    private IValidator<Client> clientValidator;

    public ClientService(IRepository<Integer, Client> repository, IValidator<Client> validator) {
        this.clientRepository = repository;
        this.clientValidator = validator;
    }

    public void addClient(Integer id, String firstname, String lastname, String email, String phone) throws RepositoryException, ValidationException {
        Client client = new Client(id, firstname, lastname, email, phone);
        clientValidator.validate(client);
        clientRepository.add(client);
    }

    public void deleteClient(Integer id) throws RepositoryException {
        clientRepository.delete(id);
    }

    public void updateClient(Integer id, String firstname, String lastname, String email, String phone) throws RepositoryException, ValidationException {
        Client client = new Client(id, firstname, lastname, email, phone);
        clientValidator.validate(client);
        clientRepository.update(client);
    }

    public ArrayList<Client> getAllClients() {
        return clientRepository.getAll();
    }

    public ArrayList<Client> filter(IFilter<Client> clientFilter) {
        ArrayList<Client> filtered = new ArrayList<>();
        for (Client client : clientRepository.getAll()) {
            if (clientFilter.accept(client)) {
                filtered.add(client);
            }
        }
        return filtered;
    }
}