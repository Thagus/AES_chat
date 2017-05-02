package client;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Created by Thagus on 01/05/17.
 */
public class ChatView {
    private DecryptionController decryptionController;
    private TableView<Message> messageTableView;

    public Scene createScene(SocketHandler socketHandler, MessageHandler messageHandler){
        this.decryptionController = new DecryptionController();

        VBox layout = new VBox(5);

        createChatArea(layout, messageHandler);
        createChatField(layout, socketHandler);

        return new Scene(layout, 720, 480);
    }

    private void createChatArea(VBox layout, MessageHandler messageHandler){
        messageTableView = new TableView<>();

        TableColumn<Message, String> messageColumn = new TableColumn<>();
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("displayMessage"));

        //Wrap text in cells
        messageColumn.setCellFactory(param -> {
            TableCell<Message, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(messageColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        messageTableView.getColumns().add(messageColumn);

        //Handle clicking on a message
        messageTableView.setOnMouseClicked(decryptionController::handleTableClick);

        messageTableView.setItems(messageHandler.getMessages());
        messageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Add table to layout
        layout.getChildren().add(messageTableView);
    }

    private void createChatField(VBox layout, SocketHandler socketHandler){
        HBox chatBarLayout = new HBox(8);

        TextField chatTextField = new TextField();
        chatTextField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                String msj = chatTextField.getText();
                if(msj.length()>0){
                    try {
                        socketHandler.sendMessage(msj);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                chatTextField.setText("");
            }
        });

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String msj = chatTextField.getText();
            if(msj.length()>0){
                try {
                    socketHandler.sendMessage(msj);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            chatTextField.setText("");
        });

        chatBarLayout.getChildren().addAll(chatTextField, sendButton);

        layout.getChildren().add(chatBarLayout);
    }

    public void reloadTable(){
        messageTableView.refresh();
    }
}
