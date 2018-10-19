package NegaTiV.ChatServer;

import NegaTiV.ChatClient.Message;
import NegaTiV.ChatClient.ServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class SingleThreadServer extends Thread{
    private User user;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private boolean isRunning;

    public SingleThreadServer(Socket socket) throws IOException
    {
        this.socket = socket;
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        int UserID = MultiThreadServer.GetClientsNumber() + 1;
        user = new User("User" + Integer.toString(UserID));
        user.setID(UserID);
        System.out.println("Установлено новое соединение");
        isRunning = true;
        start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        Message msg;
        while (isRunning)
        {
            try
            {
                msg = (Message)inputStream.readObject();
                System.out.println("Пользователь " + getUser().getName() + " отправил сообщение: " + msg.getValue());
                switch (msg.getType())
                {
                    case LOGIN:
                        Login(msg);
                        break;
                    case MSG:
                        PareseMSG(msg);
                        break;
                    case QUIT:
                        Quit();
                        break;
                    case PING:
                        Ping();
                        break;
                    case HELP:
                        Help();
                        break;
                }


            }
            catch (IOException | ClassNotFoundException e)
            {
                System.out.println("Что то пошло не так: " + e.toString());
                SendToAll(new ServerMessage("SERVER", "Пользователь " + getUser().getName() + " покинул чат."));
                Close();
            }
        }

    }

    private void Login(Message msg)
    {
        int ID = MultiThreadServer.FindByName(msg.getValue());
        if ((ID == -1) && (!msg.getValue().equalsIgnoreCase("SERVER")))
        {
            System.out.println("Пользователь " + getUser().getName() + " сменил имя на " + msg.getValue());
            user.setName(msg.getValue());
            Send(new ServerMessage("SERVER", "SUCCESSFULLY"));
            SendToAll(new ServerMessage("SERVER", "Пользователь " + getUser().getName() + " зашёл чат."));
        }
        else
            Send(new ServerMessage("SERVER", "UNSUCCESSFULLY"));
    }

    private void PareseMSG(Message msg)
    {
        StringBuilder str = new StringBuilder(msg.getValue());
        while(str.charAt(0) == ' ')
            str.deleteCharAt(0);
        if (str.charAt(0) == '@')
        {
            int i = str.indexOf(" ");
            if (i != -1)
            {
                StringBuilder name = new StringBuilder(str.substring(1, i));
                StringBuilder strmsg = new StringBuilder(str.substring(i));
                if ((i = MultiThreadServer.FindByName(name.toString())) != -1)
                {
                    MultiThreadServer.UserManagerList.get(i - 1).Send(new ServerMessage(getUser().getName(),  "(лично вам):" + strmsg.toString()));
                    Send(new ServerMessage(getUser().getName(),"(для " + name.toString() + "):" + strmsg.toString()));
                }
                else
                    Send(new ServerMessage("SERVER", "Ошибка: Пользователя с именем " + name.toString() + " не существует."));
            }
            else
                Send(new ServerMessage("SERVER", "Ошибка: Вы не ввели сообщение"));
        }
        else
        {
            String strMsg = getUser().getName() + ": " + msg.getValue();
            SendToAll(new ServerMessage(getUser().getName(), msg.getValue()));
        }
    }

    private void SendToAll(ServerMessage msg)
    {
        for(SingleThreadServer sts : MultiThreadServer.UserManagerList)
            sts.Send(msg);
    }

    private void Ping()
    {
        Send(new ServerMessage("SERVER", "Ping"));
    }

    private void Quit()
    {
        SendToAll(new ServerMessage("SERVER", "Пользователь " + getUser().getName() + " покинул чат."));
        Close();
    }

    private void Help()
    {
        Send(new ServerMessage("SERVER", "Sorry, nothing can help you."));
    }

    void Send(ServerMessage msg)
    {
        try
        {
            outputStream.writeObject(msg);
            outputStream.flush();
        }
        catch (IOException e)
        {
            System.out.println("Сообщение пользователя "  + this.getUser().getName() + " не отправлено: \n" + e.toString());
        }
    }


    void Close()
    {
        System.out.println("Пользователь " + getUser().getName() + " отключился");
        MultiThreadServer.UserManagerList.remove(this);
        isRunning = false;
    }

}
