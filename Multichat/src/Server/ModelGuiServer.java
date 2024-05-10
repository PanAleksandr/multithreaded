package Server;

import Connection.Connection;

import java.util.HashMap;
import java.util.Map;

public class ModelGuiServer {
    // Šiame modelyje saugoma žemėlapis (map) su visais prijungtais klientais.
    // Raktas yra kliento vardas, o reikšmė yra `Connection` objektas, atstovaujantis kliento ryšį.
    private Map<String, Connection> allUsersMultiChat = new HashMap<String, Connection>(); /// Naudojamas `HashMap` žemėlapis

    // Grąžina visą žemėlapį su prijungtais klientais.
    protected Map<String, Connection> getAllUsersMultiChat() {
        return allUsersMultiChat;
    }
    // Prideda naują vartotoją į žemėlapį.
    protected void addUser(String nameUser, Connection connection) {
        allUsersMultiChat.put(nameUser, connection);
    }
    // Pašalina vartotoją iš žemėlapio pagal vardą.
    protected void removeUser(String nameUser) {
        allUsersMultiChat.remove(nameUser);
    }
}
