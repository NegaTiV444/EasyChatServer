package NegaTiV.ChatClient;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Updater extends Thread{

   private ObjectInputStream InputStream;
   private UpdaterAction ua;

   public Updater(ObjectInputStream InputStream)
   {
       this.InputStream = InputStream;
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
