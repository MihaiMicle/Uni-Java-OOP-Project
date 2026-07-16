package repository;

import domain.Client;
import java.sql.*;
import java.util.ArrayList;

public class DBClientsRepository implements IRepository<Integer, Client> {
    private String URL;
    private Connection conn = null;

    public DBClientsRepository(String URL) {
        this.URL = "jdbc:sqlite:" + URL;
        createTable();
    }

    private void createTable() {
        openConnection();
        String sql = "CREATE TABLE IF NOT EXISTS Clients (" +
                "id INTEGER PRIMARY KEY, " +
                "firstname TEXT, " +
                "lastname TEXT, " +
                "email TEXT, " +
                "phone TEXT);";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void openConnection() {
        try {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection(this.URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        try {
            if (conn != null && conn.isClosed()) return;
            else if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Client elem) throws RepositoryException {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Clients VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, elem.getID());
            ps.setString(2, elem.getFirstname());
            ps.setString(3, elem.getLastname());
            ps.setString(4, elem.getEmail());
            ps.setString(5, elem.getPhone());
            ps.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new RepositoryException("Error adding client: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) throws RepositoryException {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Clients WHERE id = ?;")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting client: " + e.getMessage());
        }
    }

    @Override
    public void update(Client elem) throws RepositoryException {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Clients SET firstname = ?, lastname = ?, email = ?, phone = ? WHERE id = ?;")) {
            ps.setString(1, elem.getFirstname());
            ps.setString(2, elem.getLastname());
            ps.setString(3, elem.getEmail());
            ps.setString(4, elem.getPhone());
            ps.setInt(5, elem.getID());
            ps.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating client: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Client> getAll() {
        ArrayList<Client> clients = new ArrayList<>();
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Clients;");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                clients.add(new Client(id, firstname, lastname, email, phone));
            }
            closeConnection();
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Client getByID(Integer id) {
        openConnection();
        Client client = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Clients WHERE id = ?;")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String firstname = rs.getString("firstname");
                    String lastname = rs.getString("lastname");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    client = new Client(id, firstname, lastname, email, phone);
                }
            }
            closeConnection();
            return client;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}