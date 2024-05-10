package Client;

import java.util.HashSet;
import java.util.Set;

public class ModelGuiClient { // klasėje saugomas rinkinys prijungtų vartotojų

    private Set<String> users = new HashSet<String>(); // Sukuriamas rinkinys su vartotojų vardais
    // Metodas grąžina dabartinį vartotojų rinkinį.
    protected Set<String> getUsers() {
        return users;// Grąžina vartotojų rinkinį
    }

    protected void addUser(String nameUser) {
        users.add(nameUser); //Prideda vartotoją į rinkinį
    }

    protected void removeUser(String nameUser) {
        users.remove(nameUser); // Pašalina vartotoją iš rinkinio
    }

    protected void setUsers(Set<String> users) {
        this.users = users; // Pakeičia esamą rinkinį nauju rinkiniu
    }
}
