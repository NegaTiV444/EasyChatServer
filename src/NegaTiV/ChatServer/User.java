package NegaTiV.ChatServer;

public class User {
    private String name;
    private int ID;

    public User(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }
}
