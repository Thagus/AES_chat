package client;

import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

/**
 * Created by Thagus on 01/05/17.
 */
public class DecryptionController {
    public void handleTableClick(MouseEvent event){
        //Single click on row with primary button
        if(event.getClickCount() == 1 && event.getButton()== MouseButton.PRIMARY){
            Node node = ((Node) event.getTarget()).getParent();
            TableRow row;
            if (node instanceof TableRow) {
                row = (TableRow) node;

                //Extract Message from table
                Message message = (Message) row.getItem();

                requestDecryption(message);
            } else if (node.getParent() instanceof TableRow){
                //clicking on text part
                row = (TableRow) node.getParent();

                //Extract Message from table
                Message message = (Message) row.getItem();

                requestDecryption(message);
            }
        }
    }

    private void requestDecryption(Message message){
        //Ask for the key
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Decrypt message");
        dialog.setHeaderText("Insert decryption key");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(key -> {
            try {
                message.decypt(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
