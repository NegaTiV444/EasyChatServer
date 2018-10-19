package NegaTiV.ChatClient;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    private String userName;
    private String userMessage;

    public ServerMessage(String userName, String userMessage)
    {
        this.userMessage = userMessage;
        this.userName = userName;
    }


    public String getUserMessage() {
        return userMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
