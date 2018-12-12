package NegaTiV.ChatServer;

import NegaTiV.ChatClient.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/*
    V 3.0
    ChatCerver
    Made by NegaTiV444 (Dubovski V.)
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
                                System.out.println("Total users: " + UserManagerList.size());
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
        System.out.println("Server started");
        try
        {
            while (isRun)
            {
                Socket socket = server.accept();
                TestUsers();
                try
                {
                    System.out.println("Someone try to connect");
                    UserManagerList.add(new SingleThreadServer(socket));
                }
                catch (IOException e)
                {
                    socket.close();
                    System.out.println("Something went wrong: " + e.toString());
                }
            }
        } finally
        {
            server.close();
        }
    }

    public static int GetClientsNumber()
    {
        return UserManagerList.size();
    }

    public static SingleThreadServer FindByName(String name)
    {
        for(SingleThreadServer sts : UserManagerList)
        {
            if (sts.getUser().getName().equals(name))
                return sts;
        }
        return null;
    }

    private static void ServerStop(ServerSocket server)
    {
        for(SingleThreadServer sts : UserManagerList)
        {
            sts.Send(new ServerMessage("SERVER", "Server was stopped"));
            sts.Close();
        }
        UserManagerList.clear();
        isRun = false;
        System.out.println("Server stopped.");
        System.exit(0);
    }

    private static void Kik(String name)
    {
        SingleThreadServer user = FindByName(name);
        if (user != null)
        {
            SendToAll(new ServerMessage("SERVER", name + " was kicked by SERVER"));
            user.Close();
        }
    }

    static void SendToAll(ServerMessage msg)
    {
        for(SingleThreadServer sts : MultiThreadServer.UserManagerList)
            sts.Send(msg);
    }

     static void TestUsers()
    {
        for(SingleThreadServer sts : MultiThreadServer.UserManagerList)
            sts.Test();
    }

}