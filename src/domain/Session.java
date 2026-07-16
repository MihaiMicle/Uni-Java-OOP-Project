package domain;

import java.io.Serializable;
import java.util.Objects;

public class Session implements Identifiable<Integer>, Serializable {
    private Integer id;
    private Integer clientID;
    private String date;
    private String time;
    private String workoutDescription;

    public Session() {}

    public Session(Integer id, Integer clientID, String date, String time, String workoutDescription) {
        this.id = id;
        this.clientID = clientID;
        this.date = date;
        this.time = time;
        this.workoutDescription = workoutDescription;
    }

    public Integer getID() {

        return id;
    }

    public void setID(Integer id) {

        this.id = id;
    }

    public Integer getClientID() {

        return clientID;
    }

    public void setClientID(Integer clientID) {

        this.clientID = clientID;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWorkoutDescription() {

        return workoutDescription;
    }

    public void setWorkoutDescription(String workoutDescription) {

        this.workoutDescription = workoutDescription;
    }

    @Override
    public String toString() {
        return "Session{" + "ID: " + id + ", Client ID: " + clientID + ", Date: " + date + ", Time: " + time + ", Workout Description: " + workoutDescription + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass())
            return false;

        Session session = (Session) other;
        return this.id == session.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
