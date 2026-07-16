package domain;

import java.util.Objects;
import java.io.Serializable;

public class Client implements Identifiable<Integer>, Serializable {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    public Client() {}

    public Client(Integer id, String firstname, String lastname, String email, String phone) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString(){
        return "Client{" + "ID: " + id + ", Fistname: " + firstname + ", Lastname: " + lastname + ", Email: " + email + ", Phone: " + phone + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null || getClass() != other.getClass())
            return false;
        Client client = (Client) other;
        return this.id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

