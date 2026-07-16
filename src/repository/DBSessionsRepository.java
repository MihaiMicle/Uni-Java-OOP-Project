package repository;

import domain.Session;
import java.sql.*;
import java.util.ArrayList;

public class DBSessionsRepository implements IRepository<Integer, Session> {
    private String URL;
    private Connection conn = null;

    public DBSessionsRepository(String URL) {
        this.URL = "jdbc:sqlite:" + URL;
        createTable();
    }

    private void createTable() {
        openConnection();
        String sql = "CREATE TABLE IF NOT EXISTS Sessions (" +
                "id INTEGER PRIMARY KEY, " +
                "clientID INTEGER, " +
                "date TEXT, " +
                "time TEXT, " +
                "workoutDescription TEXT);";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
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
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Session elem) throws RepositoryException {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Sessions (id, clientID, date, time, workoutDescription) VALUES (?, ?, ?, ?, ?);")) {
            ps.setInt(1, elem.getID());
            ps.setInt(2, elem.getClientID());
            ps.setString(3, elem.getDate());
            ps.setString(4, elem.getTime());
            ps.setString(5, elem.getWorkoutDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error adding session: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public void delete(Integer id) throws RepositoryException {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Sessions WHERE id = ?;")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting session: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }


    @Override
    public void update(Session elem) throws RepositoryException {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Sessions SET clientID = ?, date = ?, time = ?, workoutDescription = ? WHERE id = ?;")) {
            ps.setInt(1, elem.getClientID());
            ps.setString(2, elem.getDate());
            ps.setString(3, elem.getTime());
            ps.setString(4, elem.getWorkoutDescription());
            ps.setInt(5, elem.getID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating session: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public ArrayList<Session> getAll() {
        openConnection();
        ArrayList<Session> sessions = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Sessions;");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int clientID = rs.getInt("clientID");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String description = rs.getString("workoutDescription");

                Session session = new Session(id, clientID, date, time, description);
                sessions.add(session);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return sessions;
    }

    @Override
    public Session getByID(Integer id) {
        openConnection();
        Session session = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Sessions WHERE id = ?;")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int clientID = rs.getInt("clientID");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String description = rs.getString("workoutDescription");
                    session = new Session(id, clientID, date, time, description);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return session;
    }
}