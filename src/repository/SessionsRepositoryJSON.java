package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Session;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SessionsRepositoryJSON extends FileRepository<Integer, Session> {
    private ObjectMapper mapper = new ObjectMapper();

    public SessionsRepositoryJSON(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        this.map = new HashMap<>();
        File file = new File(this.filename);
        if (!file.exists()) return;

        try {
            List<Session> sessions = mapper.readValue(file, new TypeReference<List<Session>>() {});
            for (Session s : sessions) {
                this.map.put(s.getID(), s);
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