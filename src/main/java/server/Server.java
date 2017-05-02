package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String args[]) throws IOException {
        System.out.println("Server Listening......");

        ServerSocket ss2 = new ServerSocket(4815);
        ChatManager chatManager = new ChatManager();

        while(true){
            try{
                Socket s = ss2.accept();
                System.out.println("connection Established");
                ServerThread st = new ServerThread(s, chatManager);
                st.start();
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");
            }
        }
    }
}

class ServerThread extends Thread{
    private BufferedReader inputStream = null;
    private PrintWriter outputStream = null;
    private Socket socket = null;
    private ChatManager chatManager;
    private boolean nameSet = false;

    public ServerThread(Socket socket, ChatManager chatManager){
        this.socket = socket;
        this.chatManager = chatManager;
        chatManager.registerThread(this);

        try{
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream());
        }
        catch(IOException e){
            System.out.println("IO error in server thread");
        }
    }

    public void run() {
        String line;
        try {
            line = inputStream.readLine();
            while(line != null){
                if(!nameSet){
                    this.setName(line);
                    nameSet = true;
                    System.out.println("Registered user: " + line);
                }
                else {
                    //Compose message
                    line = this.getName() + " " + line;
                    chatManager.sendMessage(line);
                }
                //System.out.println(this.getName() + ":  "+ line);
                line = inputStream.readLine();
            }
        } catch (IOException e) {
            line = this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Client "+ line +" terminated abruptly");
        }
        catch(NullPointerException e){
            line = this.getName(); //reused String line for getting thread name
            System.out.println("Client "+ line +" Closed");
        } finally{
            try{
                System.out.println("Connection Closing..");
                if (inputStream != null){
                    inputStream.close();
                    System.out.println("Socket Input Stream Closed");
                }

                if(outputStream != null){
                    outputStream.close();
                    System.out.println("Socket Out Closed");
                }

                if (socket != null){
                    socket.close();
                    System.out.println("Socket Closed");
                }
            }
            catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }
    }

    public synchronized void relayMessage(String message){
        outputStream.println(message);
        outputStream.flush();
    }
}