package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Thagus on 01/05/17.
 */
public class MessageHandler{
    private ObservableList<Message> messages;
    private ChatView chatView;

    public MessageHandler(){
        messages = FXCollections.observableArrayList();
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    public void reloadView(){
        chatView.reloadTable();
    }
}
