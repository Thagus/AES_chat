package client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Thagus on 01/05/17.
 */
public class Message {
    private String text;
    private String ciphertext;

    private String displayMessage;  //The message to display, depending if we have the original text or just the ciphered one
    private String ownerName;

    private MessageHandler messageHandler;

    private final int MAX_KEY_SIZE = 16;

    public Message(String message, String ownerName, boolean isCiphered, MessageHandler messageHandler){
        this.ownerName = ownerName;
        this.messageHandler = messageHandler;

        if(isCiphered){
            this.ciphertext = message;
        }
        else {
            this.text = message;
        }

        this.displayMessage = ownerName + ": " + message;
    }

    public String encrypt(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        key = validateKey(key);

        //Create key and cipher
        SecretKeySpec aesKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        //Encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());

        ciphertext = Base64.getEncoder().encodeToString(encrypted);

        return ciphertext;
    }

    public String decypt(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        key = validateKey(key);

        //Create key and cipher
        SecretKeySpec aesKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        //Decrypt text
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

        text = new String(original);
        setDisplayMessage(text);

        return text;
    }

    public String validateKey(String key){
        if(key.length()==MAX_KEY_SIZE){
            return key;
        }
        else if(key.length()<MAX_KEY_SIZE){ //Fill the key with zeros
            return String.format("%-"+MAX_KEY_SIZE+"s", key);
        }
        else {  //Cut the key to fit in max length
            return key.substring(0, MAX_KEY_SIZE);
        }
    }

    public String getText() {
        return text;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String text){
        this.displayMessage = ownerName + ": " + text;
        messageHandler.reloadView();
    }
}
