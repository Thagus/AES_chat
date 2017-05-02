package client;

import javafx.scene.control.TextInputDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

/**
 * Created by Thagus on 01/05/17.
 */
public class SocketHandler extends Thread {
    private BufferedReader inputStream = null;
    private PrintWriter outputStream = null;
    private Socket socket = null;

    private MessageHandler messageHandler;

    public SocketHandler(MessageHandler messageHandler) throws Exception{
        this.messageHandler = messageHandler;

        InetAddress address = InetAddress.getLocalHost();

        try {
            socket = new Socket(address, 4815);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream());
        }
        catch (IOException e){
            e.printStackTrace();
            System.err.print("IO Exception");
        }
    }

    public void setClientName(String name){
        this.setName(name);
        System.out.println("Name set to: " + name);
        outputStream.println(name);
        outputStream.flush();
    }

    public void run(){  //Receive messages
        try{
            String response = inputStream.readLine();
            while(response.compareTo("QUIT")!=0){

                if(!response.startsWith(this.getName())){   //The message wasnt sent by us, therefore we need to add the received ciphered text to the message handler
                    String[] split = response.split("\\s+");
                    Message message = new Message(split[1], split[0], true, messageHandler);
                    messageHandler.addMessage(message);
                }

                response = inputStream.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Socket read Error");
        }
        finally{
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
                System.out.println("Connection Closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String text) throws Exception{
        try{
            Message message = new Message(text, getName(), false, messageHandler);
            //Ask for the key
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Encrypt message");
            dialog.setHeaderText("Insert encryption key");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(key -> {
                try {
                    String lineToSend = message.encrypt(key);

                    outputStream.println(lineToSend);
                    outputStream.flush();

                    messageHandler.addMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
