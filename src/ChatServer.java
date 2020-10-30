import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatServer {
    ArrayList <PrintWriter> clientOutputStream;

    public static void main(String[] args) {
        new ChatServer().go();
    }


    public void go(){
        try{
        ServerSocket ss = new ServerSocket(6668);
        clientOutputStream = new ArrayList<>();
        while(true){
            Socket socket = ss.accept();
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            clientOutputStream.add(pw);

            Thread t = new Thread(new ClientHandler(socket));
            t.start();
            System.out.println("connection established");
            }
        }
        catch (Exception exc){exc.printStackTrace();}
    }

    public class ClientHandler implements Runnable{
        BufferedReader bfr;
        Socket socket;
        ClientHandler(Socket socket){
            try{this.socket = socket;
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));}
            catch (Exception exc){exc.printStackTrace();}
        }

        @Override
        public void run() {
            try{
                String message;
                while ((message = bfr.readLine()) !=null){
                    tellEveryone(message);
                }
            }catch (Exception exc){exc.printStackTrace();}
        }
    }

    public void tellEveryone(String message){
        for(PrintWriter pw : clientOutputStream){
            pw.println(message);
            pw.flush();
        }
    }
}
