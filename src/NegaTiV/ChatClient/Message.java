package NegaTiV.ChatClient;

import java.io.Serializable;

public class Message implements Serializable {

    public enum MsgType
    {
        LOGIN,
        MSG,
        QUIT,
        PING,
        HELP
    }

    private MsgType type;
    private String value;

    public Message(MsgType type, String value)
    {
     this.type = type;
     this.value = value;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
