package Connection;

import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {
    private MessageType typeMessage; // Žinutės tipas
    private String textMessage; // Saugo žinutės tekstinį turinį
    private Set<String> listUsers; // Saugo vartotojų vardų rinkinį

    // Konstruktor, kuris sukuria žinutę su tipu ir tekstu
    public Message(MessageType typeMessage, String textMessage) {
        this.textMessage = textMessage;
        this.typeMessage = typeMessage;
        this.listUsers = null;
    }
    // Konstruktor, kuris sukuria žinutę su tipu ir vartotojų sąrašu
    public Message(MessageType typeMessage, Set<String> listUsers) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.listUsers = listUsers;
    }
    // Konstruktor, kuris sukuria žinutę su tik tipu
    public Message(MessageType typeMessage) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.listUsers = null;
    }
    // Getteris, grąžinantis žinutės tipą
    public MessageType getTypeMessage() {
        return typeMessage;
    }
    // Getteris, grąžinantis vartotojų sąrašą
    public Set<String> getListUsers() {
        return listUsers;
    }
    // Getteris, grąžinantis žinutės tekstą
    public String getTextMessage() {
        return textMessage;
    }

}