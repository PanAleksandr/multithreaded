package Connection;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable {
    private final Socket socket; // Ryšio su serveriu atvaizdavimas
    private final ObjectOutputStream out; // Išvesties srautas
    private final ObjectInputStream in; // Įvesties srautas

    public Connection(Socket socket) throws IOException {
        // Patikrina, kad soketas nėra null ir nėra uždarytas
        if (socket == null || socket.isClosed()) {
            throw new IllegalArgumentException("Socket cant be null or closed");
        }

        this.socket = socket;

        // Inicijuojame įvesties ir išvesties srautus
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    // Metodas žinutėms siųsti
    public void send(Message message) throws IOException {

        if (socket == null || socket.isClosed() || out == null) {
            throw new IOException("The socket is closed or not initialized.");
        }

        synchronized (this.out) { // Sinchronizacija siekiant išvengti konfliktų daugiagijoje++++++++++++++++++++++++++
            out.writeObject(message); // Siunčiame objektą per išvesties srautą
        }
    }

    // Metodas žinutėms gauti
    public Message receive() throws IOException, ClassNotFoundException {
        if (socket == null && socket.isClosed() || in == null) {
            throw new IOException("Socket or stream is not initialized");
        }

        synchronized (this.in) { // Sinchronizacija siekiant išvengti konfliktų daugiagijoje+++++++++++++
            return (Message) in.readObject(); // Skaityti objektą iš įvesties srauto
        }
    }

    // Metodas uždaryti ryšį
    @Override
    public void close() throws IOException {
        // Uždaryti srautus tik jei jie inicijuoti
        if (in != null) {
            in.close();  // Uždaryti įvesties srautą
        }

        if (out != null) {
            out.close();  // Uždaryti išvesties srautą
        }


        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
