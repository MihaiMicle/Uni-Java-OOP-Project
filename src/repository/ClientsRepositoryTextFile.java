package repository;

import domain.Client;
import java.io.*;
import java.util.HashMap;


public class ClientsRepositoryTextFile extends FileRepository<Integer, Client> {
    public ClientsRepositoryTextFile(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        this.map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.filename))) {
            String line=br.readLine();
            while (line!=null) {
                String[] tokens = line.split(",");
                if(tokens.length!=5)
                    continue;
                int id = Integer.parseInt(tokens[0].trim());
                String firstname = tokens[1].trim();
                String lastname = tokens[2].trim();
                String email = tokens[3].trim();
                String phone = tokens[4].trim();
                Client client = new Client(id, firstname, lastname, email, phone);
                this.map.put(id, client);
                line=br.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found, initializing empty repository: " + this.filename);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filename))) {
            for (Client client : this.map.values()) {
                String line = client.getID() + "," + client.getFirstname() + "," + client.getLastname() + "," + client.getEmail() + "," + client.getPhone();

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
