package Client;

import Connection.*;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection connection; // Saugo ryšį su serveriu
    private ModelGuiClient model; // Laiko modelį GUI daliai
    private ViewGuiClient gui; // Laiko vartotojo sąsajos objektą
    private volatile boolean isConnect = false; //ar klientas yra prijungtas prie serverio

    // Konstruktor, kuris inicijuoja modelį ir vartotojo sąsają
    public Client() {

        model = new ModelGuiClient();//naujas modelis objektas
        gui = new ViewGuiClient(this);// sukuriama gui sasaja
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }
    // Metodas pradėti klientą
    public void start() {
        gui.initFrameClient(); // Inicijuoja GUI sąsają

        while (true) { // Nuolatinis ciklas, kol klientas aktyvus
            if (isConnect) {
                try {
                    nameUserRegistration(); // Atlieka vartotojo registraciją
                    receiveMessageFromServer(); // Priima žinutes iš serverio
                } catch (Exception e) {
                    gui.errorDialogWindow("Error: " + e.getMessage());
                    setConnect(false); // Atsijungia, jei įvyko klaida
                    closeConnection();
                }
            }

            // Maža pauzė, kad sumažinti procesoriaus apkrovą
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Pertrauktas siūlelis
            }
        }
    }

    // Metodas prisijungti prie serverio
    protected void connectToServer() {
        if (!isConnect) { //jei nebuvo prisijugtas
            try {
                String address = gui.getServerAddressFromOptionPane(); // Gauti serverio adresą
                int port = gui.getPortServerFromOptionPane(); // Gauti serverio portą


                Socket socket = new Socket(address, port);  //+++ Sukurti naują soketą
                connection = new Connection(socket); // // Sukurti ryšį
                isConnect = true;

                gui.addMessage("Connection to the service is successful");
            } catch (IOException e) {
                gui.errorDialogWindow("Error connecting to service: " + e.getMessage());
                connection = null; // сброс соединения при ошибке
            }
        } else {
            gui.errorDialogWindow("You are already connected");
        }
    }

    // Metodas vartotojo registracijai
    protected void nameUserRegistration() throws ClassNotFoundException {
        boolean registered = false;
        while (!registered) {
            try {
                Message message = connection.receive(); // Priima žinutę iš serverio
                if (message.getTypeMessage() == MessageType.REQUEST_NAME_USER) { // Jei reikia vardo
                    String nameUser = gui.getNameUser(); // Gauti vartotojo vardą
                    connection.send(new Message(MessageType.USER_NAME, nameUser)); // Siunčia vardą
                } else if (message.getTypeMessage() == MessageType.NAME_USED) { // Jei vardas naudojamas
                    gui.errorDialogWindow("Name is already exist,choose another.");
                } else if (message.getTypeMessage() == MessageType.NAME_ACCEPTED) {
                    gui.addMessage("Name accept.");
                    registered = true;
                }
            } catch (IOException e) {
                gui.errorDialogWindow("Error with registration: " + e.getMessage());
                setConnect(false);
                closeConnection();
            }
        }
    }

    // Metodas siųsti žinutę į serverį
    protected void sendMessageOnServer(String text) {
        if (isConnect && connection != null) { // Tik jei yra ryšys
            try {
                connection.send(new Message(MessageType.TEXT_MESSAGE, text));
            } catch (IOException e) {
                gui.errorDialogWindow("Error with sending message");
            }
        } else {
            gui.errorDialogWindow("You are not connected.");
        }
    }

    // Metodas priimti žinutes iš serverio
    protected void receiveMessageFromServer() throws ClassNotFoundException {
        while (isConnect) {
            try {
                Message message = connection.receive(); // Priima žinutę iš serverio
                if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) { // Jei tai tekstinė žinutė
                    gui.addMessage(message.getTextMessage()); // Prideda žinutę į GUI
                } else if (message.getTypeMessage() == MessageType.USER_ADDED) { // Jei vartotojas pridėtas
                    model.addUser(message.getTextMessage()); // Prideda į modelį
                    gui.refreshListUsers(model.getUsers()); // Atnaujina sąrašą GUI
                } else if (message.getTypeMessage() == MessageType.REMOVED_USER) { // Jei vartotojas pašalintas
                    model.removeUser(message.getTextMessage());
                    gui.refreshListUsers(model.getUsers());
                }
            } catch (IOException  e) {
                gui.errorDialogWindow("Error with accepting messages " + e.getMessage());
                setConnect(false);
                closeConnection();
            }
        }
    }

    // Metodas atsijungti klientą
    protected void disableClient() {
        if (isConnect && connection != null) {
            try {
                connection.send(new Message(MessageType.DISABLE_USER)); // Siunčia atsijungimo žinutę
                model.getUsers().clear(); // Išvalo vartotojų sąrašą
                setConnect(false);
                closeConnection();
            } catch (IOException e) {
                gui.errorDialogWindow("Error when disconnecting");
            }
        } else {
            gui.errorDialogWindow("You are already disconnected.");
        }
    }

    // Metodas uždaryti ryšį
    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            gui.errorDialogWindow("Error when closing connection");
        }
    }
}
