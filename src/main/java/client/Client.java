package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Client extends Application{
    private Scene authScene, chatScene;
    public static void main(String args[]){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        //Create message handler
        MessageHandler messageHandler = new MessageHandler();

        //Create chat view
        ChatView chatView = new ChatView();
        messageHandler.setChatView(chatView);

        //Create socket handler
        SocketHandler socketHandler = new SocketHandler(messageHandler);

        //Create chat scene
        chatScene = chatView.createScene(socketHandler, messageHandler);

        //Authenticate user
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userNameLabel = new Label("Username: ");
        grid.add(userNameLabel, 0, 0);
        TextField userName = new TextField();
        grid.add(userName, 1, 0);
        userName.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                String name = userName.getText();
                if(name.length()>0){
                    socketHandler.setClientName(name);    //Set the name of the client in the server
                    primaryStage.setTitle("User " + name);
                    primaryStage.setScene(chatScene);                   //Change the scene
                }
            }
        });

        Button loginButton = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 2);
        loginButton.setOnAction(e -> {
            String name = userName.getText();
            if(name.length()>0){
                socketHandler.setClientName(name);    //Set the name of the client in the server
                primaryStage.setTitle("User " + name);
                primaryStage.setScene(chatScene);                   //Change the scene
            }
        });

        authScene = new Scene(grid, 300, 275);

        socketHandler.start();
        //Initialize window
        primaryStage.setScene(authScene);
        primaryStage.show();

        //Start receiving messages
        //socketHandler.receiveMessages();
    }
}