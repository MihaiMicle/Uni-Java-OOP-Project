package repository;

import domain.Client;
import java.io.*;
import java.util.HashMap;

public class ClientsRepositoryBinaryFile extends FileRepository<Integer, Client> {
    public ClientsRepositoryBinaryFile(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.filename))) {
            this.map = (HashMap<Integer, Client>) ois.readObject();

        } catch (FileNotFoundException | EOFException e) {
            System.out.println("Binary file not found or empty, initializing new repository: " + this.filename);
            this.map = new HashMap<>();
            // --- END FIX ---

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filename))) {
            oos.writeObject(this.map);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
