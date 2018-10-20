package NegaTiV.ChatServer;

import NegaTiV.ChatClient.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/*
    V 3.0
    ChatCerver
    Made by NegaTiV (Dubovski V.)
    16/10/2018
    Use only with ChatClient V 3.0
 */

public class MultiThreadServer {

    public static final int PORT = 6666;
    static LinkedList<SingleThreadServer> UserManagerList = new LinkedList<>();
    private static boolean isRun = true;

    public static void main(String[] args) throws IOException
    {
        ServerSocket server = new ServerSocket(PORT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream input = System.in;
                Reader reader = new InputStreamReader(input);
                BufferedReader buffReader = new BufferedReader(reader);
                String command;
                while (isRun)
                {
                    try
                    {
                        command = buffReader.readLine();
                        StringBuilder checkKik = new StringBuilder(command);
                        if (checkKik.substring(0, 4).equalsIgnoreCase("/kik"))
                        {
                            StringBuilder name = new StringBuilder(checkKik.substring(5));
                            Kik(name.toString());
                        }
                        switch (command)
                        {
                            case "/userlist":
                                System.out.println("Всего пользователей: " + UserManagerList.size());
                                for(SingleThreadServer sts : UserManagerList)
                                    System.out.println(sts.getUser().getName());
                                break;
                            case "/stop":
                                ServerStop(server);
                                break;
                        }

                    }
                    catch (IOException e)
                    {

                    }
                }
            }
        }).start();
        System.out.println("Сервер запущен");
        try
        {
            while (isRun)
            {
                Socket socket = server.accept();
                try
                {
                    System.out.println("Кто-то пытается подключиться");
                    UserManagerList.add(new SingleThreadServer(socket));
                }
                catch (IOException e)
                {
                    socket.close();
                    System.out.println("Что-то пошло не так: " + e.toString());
                }
            }
        }
        finally
        {
            server.close();
        }
    }

    public static int GetClientsNumber()
    {
        return UserManagerList.size();
    }

    public static int FindByName(String name)
    {
        for(SingleThreadServer sts : UserManagerList)
        {
            if (sts.getUser().getName().equals(name))
                return sts.getUser().getID() - 1;
        }
        int i = -1;
        return i;
    }

    private static void ServerStop(ServerSocket server)
    {
        for(SingleThreadServer sts : UserManagerList)
        {
            sts.Send(new ServerMessage("SERVER", "Сервер выключен"));
            sts.Close();
        }
        UserManagerList.clear();
//        try
//        {
//            server.close();
//        }
//        catch (IOException e)
//        {
//            System.out.println("Сервер закрыт" + e.toString());
//        }
        isRun = false;
        System.out.println("Сервер остановлен.");
        System.exit(0);
    }

    private static void Kik(String name)
    {
        int Index = FindByName(name);
        if (Index != -1)
        {
            SendToAll(new ServerMessage("SERVER", name + " was kiked by SERVER"));
            UserManagerList.get(Index).Close();
        }
    }

    static void SendToAll(ServerMessage msg)
    {
        for(SingleThreadServer sts : MultiThreadServer.UserManagerList)
            sts.Send(msg);
    }
}