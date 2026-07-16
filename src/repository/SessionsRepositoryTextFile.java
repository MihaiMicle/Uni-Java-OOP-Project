package repository;

import domain.Session;
import java.io.*;
import java.util.HashMap;


public class SessionsRepositoryTextFile extends FileRepository<Integer, Session> {
    public SessionsRepositoryTextFile(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        // 1. Initialize the map!
        this.map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.filename))) {
            String line=br.readLine();
            while (line!=null) {
                String[] tokens = line.split(",");
                if(tokens.length!=5)
                    continue;
                int id = Integer.parseInt(tokens[0].trim());
                int clientID = Integer.parseInt(tokens[1].trim());
                String date = tokens[2].trim();
                String time = tokens[3].trim();
                String workout = tokens[4].trim();
                Session session = new Session(id, clientID, date, time, workout);
                this.map.put(id, session);
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
            for (Session client : this.map.values()) {
                String line = client.getID() + "," + client.getClientID() + "," + client.getDate() + "," + client.getTime() + "," + client.getWorkoutDescription();

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
