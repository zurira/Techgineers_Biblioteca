package mx.edu.utez.biblioteca.model;

public class User {
    private int id;
    private String username;
    private String password;

    public User(int id, String password, String user) {
        this.id = id;
        this.password = password;
        this.username = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String user) {
        this.username = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
