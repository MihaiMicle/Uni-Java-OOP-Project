package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ClientsRepositoryJSON extends FileRepository<Integer, Client> {
    private ObjectMapper mapper = new ObjectMapper();

    public ClientsRepositoryJSON(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        this.map = new HashMap<>();
        File file = new File(this.filename);

        if (!file.exists() || file.length() == 0) {
            return;
        }

        try {
            List<Client> clients = mapper.readValue(file, new TypeReference<List<Client>>() {});
            for (Client c : clients) {
                this.map.put(c.getID(), c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeToFile() {
        try {
            mapper.writeValue(new File(this.filename), this.map.values());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}