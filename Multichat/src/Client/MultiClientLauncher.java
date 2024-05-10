package Client;


public class MultiClientLauncher {
    public static void main(String[] args) {
        // kiek reikia klientu
        for (int i = 0; i < 3; i++) {    //daugiagijos nes daug
            final int clientNumber = i + 1; // indetefikacija
            Thread thread = new Thread(new Runnable() { //Kiekvienoje for ciklo iteracijoje sukuriama nauja gija Thread
                                                        //Runnable objektą, kuris turi logiką
                @Override
                public void run() {
                    try {
                        System.out.println("Client N" + clientNumber);
                        Client client = new Client(); //new client
                        client.connectToServer(); // to server
                        client.start(); // client go
                    } catch (Exception e) { // error
                        System.err.println("Error with Client N" + clientNumber + ": " + e.getMessage());
                    }
                }
            });

            thread.start(); // Gijos paleidimas
                            //gijos paleidimas su start() sukelia asinchroninį vykdymą,
        }
    }
}
