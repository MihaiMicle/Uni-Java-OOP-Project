import domain.Client;
import domain.Session;
import javafx.application.Application;
import javafx.stage.Stage;
import repository.*;
import service.ClientService;
import service.SessionService;
import ui.GymUI;
import validator.ClientValidator;
import validator.IValidator;
import validator.SessionValidator;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        IRepository<Integer, Client> clientRepo;
        IRepository<Integer, Session> sessionRepo;

        try {
            Properties properties = new Properties();
            properties.load(new FileReader("src/settings.properties"));

            String repoType = properties.getProperty("Repository");
            if (repoType == null) repoType = "memory";

            if (repoType.equals("binary")) {
                clientRepo = new ClientsRepositoryBinaryFile(properties.getProperty("Clients_binary"));
                sessionRepo = new SessionsRepositoryBinaryFile(properties.getProperty("Sessions_binary"));
            } else if (repoType.equals("text")) {
                clientRepo = new ClientsRepositoryTextFile(properties.getProperty("Clients_text"));
                sessionRepo = new SessionsRepositoryTextFile(properties.getProperty("Sessions_text"));
            } else if (repoType.equals("xml")) {
                clientRepo = new ClientsRepositoryXML(properties.getProperty("Clients_xml"));
                sessionRepo = new SessionsRepositoryXML(properties.getProperty("Sessions_xml"));
            } else if (repoType.equals("database")) {
                clientRepo = new DBClientsRepository(properties.getProperty("Location_db"));
                sessionRepo = new DBSessionsRepository(properties.getProperty("Location_db"));
            } else if (repoType.equals("json")) {
                clientRepo = new ClientsRepositoryJSON(properties.getProperty("Clients_json"));
                sessionRepo = new SessionsRepositoryJSON(properties.getProperty("Sessions_json"));
            } else {
                clientRepo = new MemoryRepository<>();
                sessionRepo = new MemoryRepository<>();
                addSampleData(clientRepo, sessionRepo);
            }
        } catch (IOException e) {
            System.err.println("Error config: " + e.getMessage());
            clientRepo = new MemoryRepository<>();
            sessionRepo = new MemoryRepository<>();

        }

        IValidator<Client> clientValidator = new ClientValidator();
        IValidator<Session> sessionValidator = new SessionValidator();
        ClientService clientService = new ClientService(clientRepo, clientValidator);
        SessionService sessionService = new SessionService(sessionRepo, clientRepo, sessionValidator);

        GymUI.clientService = clientService;
        GymUI.sessionService = sessionService;

        try {
            new GymUI().start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void addSampleData(IRepository<Integer, Client> clientRepo, IRepository<Integer, Session> sessionRepo) {
        try {
            // Client 1: The "Most Active" (3 sessions)
            clientRepo.add(new Client(1, "John", "Active", "john@test.com", "0711111111"));

            // Client 2: A regular user (2 sessions)
            clientRepo.add(new Client(2, "Jane", "Regular", "jane@test.com", "0722222222"));

            // Client 3: Occasional user (1 session)
            clientRepo.add(new Client(3, "Bob", "Once", "bob@test.com", "0733333333"));

            // Client 4: The "Inactive" user (0 sessions) -> Should show up in "Inactive Clients" report
            clientRepo.add(new Client(4, "Larry", "Lazy", "larry@test.com", "0744444444"));


            // Scenario: "Yoga" is popular, and "15.03.2025" is the busiest day.

            // Sessions for Client 1 (John)
            sessionRepo.add(new Session(101, 1, "10.01.2025", "10:00", "Yoga")); // Jan
            sessionRepo.add(new Session(102, 1, "12.02.2025", "11:00", "HIIT")); // Feb
            sessionRepo.add(new Session(103, 1, "15.03.2025", "09:00", "Yoga")); // Mar (Busiest Day)

            // Sessions for Client 2 (Jane)
            sessionRepo.add(new Session(104, 2, "15.03.2025", "14:00", "Cardio")); // Mar (Busiest Day match)
            sessionRepo.add(new Session(105, 2, "20.03.2025", "18:00", "Yoga"));   // Mar

            // Sessions for Client 3 (Bob)
            sessionRepo.add(new Session(106, 3, "05.01.2025", "08:00", "Pilates")); // Jan

        } catch (RepositoryException e) {
            System.out.println("Error adding sample data: " + e.getMessage());
        }
    }
}