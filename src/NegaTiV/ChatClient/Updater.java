package NegaTiV.ChatClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Updater extends Thread{

    private ObjectInputStream InputStream;
    private ObjectOutputStream OutputStream;
    private UpdaterAction ua;

    public Updater(ObjectInputStream InputStream, ObjectOutputStream OutputStream)
    {
        this.InputStream = InputStream;
        this.OutputStream = OutputStream;
    }



    @Override
    public void run()
    {
        ServerMessage msg;
        while(Client.isIsConnected())
        {
            try
            {
                msg = (ServerMessage) InputStream.readObject();
                if ((msg.getUserName().equals("SERVER")) && (msg.getUserMessage().equals("TEST")) )
                {
                    OutputStream.writeObject(new Message(Message.MsgType.TEST, "TEST"));
                    OutputStream.flush();
                }
                else
                if (!((msg.getUserName().equals("SERVER")) && (msg.getUserMessage().equals("UNSUCCESSFULLY"))))
                    ua.Update(msg);
            }
            catch (IOException | ClassNotFoundException e)
            {

            }
        }
    }

    public void SetUpdaterAction(UpdaterAction ua)
    {
        this.ua = ua;
    }
}
