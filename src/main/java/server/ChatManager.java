package server;

import java.util.ArrayList;

/**
 * Created by Thagus on 01/05/17.
 */
public class ChatManager {
    ArrayList<ServerThread> servers = new ArrayList<>();

    public synchronized void registerThread(ServerThread serverThread){
        servers.add(serverThread);
    }

    public synchronized void removeThread(ServerThread serverThread){
        servers.remove(serverThread);
    }

    public synchronized void sendMessage(String message){
        for(ServerThread server : servers){
            server.relayMessage(message);
        }
    }
}
