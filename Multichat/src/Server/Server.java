package Server;

import Connection.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Server {
    private ServerSocket serverSocket; // Pagrindinis serverio soketas
    private static ViewGuiServer gui; // Grafinė sąsaja
    private static ModelGuiServer model; // Modelis, kuris saugo klientų informaciją
    private static volatile boolean isServerStart = false; // Flegma, kuri nurodo, ar serveris yra paleistas

    // Metodas, kuris paleidžia serverį
    protected void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port); // Sukuriamas naujas serverio soketas
            isServerStart = true;
            gui.refreshDialogWindowServer("Server is running.\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("Failed to start the server.\n");
        }
    }

    // Metodas, kuris sustabdo serverį
    protected void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                for (Map.Entry<String, Connection> user : model.getAllUsersMultiChat().entrySet()) {
                    user.getValue().close();
                }
                serverSocket.close();
                model.getAllUsersMultiChat().clear();
                gui.refreshDialogWindowServer("Server stopped.\n");
            } else gui.refreshDialogWindowServer("The server is not running - there is nothing to stop!\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("The server could not be stopped.\n");
        }
    }

    // Metodas, kuris priima naujas jungtis
    protected void acceptServer() {
        while (true) { //+++ Nuolatinis ciklas, kuris priima naujas jungtis
            try {
                Socket socket = serverSocket.accept(); // Priima naują jungtį
                new ServerThread(socket).start(); // +++ Sukuria ir paleidžia naują giją kiekvienam klientui
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Communication with the server has been lost.\n");
                break;
            }
        }
    }

    // Metodas, kuris siunčia žinutes visiems klientams
    protected void sendMessageAllUsers(Message message) {
        for (Map.Entry<String, Connection> user : model.getAllUsersMultiChat().entrySet()) { // +
            try {
                user.getValue().send(message); // Siunčia žinutę visiems klientams
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Error sending message to all users!\n");
            }
        }
    }

    // Pagrindinis serverio programos metodas
    public static void main(String[] args) {
        Server server = new Server();
        gui = new ViewGuiServer(server);
        model = new ModelGuiServer();
        gui.initFrameServer();
        // Nuolatinis ciklas, kuris stebi serverio būseną
        while (true) {
            if (isServerStart) { // Jei serveris yra paleistas
                server.acceptServer(); // Priima naujas jungtis
                isServerStart = false; // Iš naujo nustato flagą
            }
        }
    }

    // Klasė, kuri įgyvendina kiekvieno kliento giją
    private class ServerThread extends Thread { //+++ Nauja gija kiekvienam klientui
        private Socket socket;//connection

        public ServerThread(Socket socket) {
            this.socket = socket; // Saugomas klientų soketas
        }

        // Metodas, kuris prašo vartotojo vardo ir jį prideda prie vartotojų žemėlapio
        private String requestAndAddingUser(Connection connection) {
            while (true) { // + Nuolatinis ciklas, kuris prašo vartotojo vardo
                try {
                    connection.send(new Message(MessageType.REQUEST_NAME_USER));
                    Message responseMessage = connection.receive();
                    String userName = responseMessage.getTextMessage();
                    if (responseMessage.getTypeMessage() == MessageType.USER_NAME && userName != null && !userName.isEmpty() && !model.getAllUsersMultiChat().containsKey(userName)) {
                        model.addUser(userName, connection);//++gijos saugumos,nes daug gali noreti prideti ar pakeisti vartvarda
                        Set<String> listUsers = new HashSet<String>();
                        for (Map.Entry<String, Connection> users : model.getAllUsersMultiChat().entrySet()) {
                            listUsers.add(users.getKey());
                        }
                        connection.send(new Message(MessageType.NAME_ACCEPTED, listUsers));
                        sendMessageAllUsers(new Message(MessageType.USER_ADDED, userName));
                        return userName;
                    }
                    else connection.send(new Message(MessageType.NAME_USED));
                } catch (Exception e) {
                    gui.refreshDialogWindowServer("An error occurred when requesting and adding a new user\n");
                }
            }
        }

        // Metodas, kuris vykdo pranešimų mainus tarp klientų
        private void messagingBetweenUsers(Connection connection, String userName) {
            while (true) { // + Nuolatinis ciklas pranešimų mainams
                try {
                    Message message = connection.receive(); // Gauna žinutę
                    if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                        String textMessage = String.format("%s: %s\n", userName, message.getTextMessage());
                        sendMessageAllUsers(new Message(MessageType.TEXT_MESSAGE, textMessage));
                    }

                    if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                        sendMessageAllUsers(new Message(MessageType.REMOVED_USER, userName));
                        model.removeUser(userName);
                        connection.close();
                        gui.refreshDialogWindowServer(String.format("Remote user %s has disconnected.\n", socket.getRemoteSocketAddress()));
                        break;
                    }
                } catch (Exception e) {
                    gui.refreshDialogWindowServer(String.format("An error occurred while sending a message from user %s, or disconnected!\n", userName));
                    break;
                }
            }
        }

        @Override
        public void run() { //+++ Metodas, kuris vykdomas naujoje gijoje
            gui.refreshDialogWindowServer(String.format("A new user has connected with a remote socket - %s.\n", socket.getRemoteSocketAddress()));
            try {

                Connection connection = new Connection(socket); // Sukuria naują ryšį
                String nameUser = requestAndAddingUser(connection); // Prašo vartotojo vardo
                messagingBetweenUsers(connection, nameUser); // Pradeda pranešimų mainus tarp klientų
            } catch (Exception e) {
                gui.refreshDialogWindowServer(String.format("An error occurred while sending a message from the user!\n"));
            }
        }
    }
}