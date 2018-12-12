package NegaTiV.ChatClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*
    V 3.0
    ChatClient
    Made by NegaTiV444 (Dubovski V.)
    16/10/2018
    Use only with ChatServer V 3.0

    Для отображения принятых сообщений в метод SetUpdaterAction передать new UpdaterAction
    и переопределить метод update()
 */

public class Client {

    private static final int PORT = 6666;

    private static String IP = "93.125.49.200";
    //private static String IP = "192.168.43.197";

    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        Client.IP = IP;
    }

    private static Socket clientSocket;
    private static ObjectInputStream InputStream;
    private static ObjectOutputStream OutputStream;
    private static Updater updater;

    private static boolean isConnected = false;
    private static boolean isLogined = false;

    public static void Connect()
    {
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    InetAddress ipAddress = InetAddress.getByName(IP);
//                    InetAddress ipAddress = InetAddress.getByName("easychat.sytes.net");

                    clientSocket = new Socket(ipAddress , PORT);
                    OutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    InputStream = new ObjectInputStream(clientSocket.getInputStream());
                    updater = new Updater(InputStream, OutputStream);
                    isConnected = true;
                }
                catch (IOException e)
                {
                    isConnected = false;
                }
            }
        });
        th.start();
        try {
            th.join(4000);
            if (th.isAlive()) {
                th.interrupt();
                if (clientSocket != null)
                    clientSocket.close();
                if (InputStream != null)
                    InputStream.close();
                if (OutputStream != null)
                    OutputStream.close();
                isConnected = false;
            }
        }
        catch (InterruptedException e)
        {
            isConnected = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Login(final String name)
    {
        if (isConnected) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputStream.writeObject(new Message(Message.MsgType.LOGIN, name));
                        ServerMessage answer = (ServerMessage)InputStream.readObject();
                        if (answer.getUserMessage().equals("SUCCESSFULLY"))
                            isLogined = true;
                        else
                            isLogined = false;
                    } catch (IOException | ClassNotFoundException e) {
                        isLogined = false;
                    }
                }
            });
            th.start();
            try {
                th.join();
            }
            catch (InterruptedException e)
            {

            }
        }
    }

    public static boolean SendMessage(String msg)
    {
        if (isConnected && isLogined) {
            if (msg.charAt(0) == '/') {
                if (msg.equalsIgnoreCase("/ping"))
                    return Send(new Message(Message.MsgType.PING, ""));
                else if (msg.equalsIgnoreCase("/help"))
                    return Send(new Message(Message.MsgType.HELP, ""));
                else
                    return Send(new Message(Message.MsgType.MSG, msg));
            } else
                return Send(new Message(Message.MsgType.MSG, msg));
        }
        return false;
    }

    public static boolean Send(final Message msg)
    {
        final boolean[] res = new boolean[1];
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    OutputStream.writeObject(msg);
                    OutputStream.flush();
                    res[0] = true;
                }
                catch (IOException e)
                {
                    res[0] = false;
                }
            }
        });
        th.start();
        try
        {
            th.join();

        } catch (InterruptedException e)
        {

        }
        return res[0];
    }

    public static void setUpdaterAction(UpdaterAction ua) {
        if (isConnected && isLogined) {
            updater.SetUpdaterAction(ua);
            updater.start();
        }
    }

    public static boolean isIsConnected() {
        return isConnected;
    }

    public static boolean isIsLogined() {
        return isLogined;
    }

    public static void reset() {
        Client.isConnected = false;
        Client.isLogined = false;
    }
}
