package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class ViewGuiClient {
    private final Client client; // Nuoroda į klientą
    private JFrame frame = new JFrame("Chat");
    private JTextArea messages = new JTextArea(30, 20);// Sritis žinutėms
    private JTextArea users = new JTextArea(30, 15); // Sritis vartotojams
    private JPanel panel = new JPanel(); // Panelė mygtukams ir įvesties laukui
    private JTextField textField = new JTextField(40); // Laukas žinutėms įvesti
    private JButton buttonDisable = new JButton("Disconnect");
    private JButton buttonConnect = new JButton("Connect");

    public ViewGuiClient(Client client) {
        this.client = client; // Kliento priskyrimas
    }

    // Metodas, skirtas nustatyti GUI klientų programai
    protected void initFrameClient() {
        messages.setEditable(false);  // Žinutės negali būti redaguojamos
        users.setEditable(false); // Vartotojų sąrašas negali būti redaguojamas
        frame.add(new JScrollPane(messages), BorderLayout.CENTER); // Pridedame žinučių sritį su slinkties juosta
        frame.add(new JScrollPane(users), BorderLayout.EAST); // Pridedame vartotojų sąrašą su slinkties juosta
        panel.add(textField); // Pridedame teksto lauką į panelę
        panel.add(buttonConnect); // Pridedame prisijungimo mygtuką į panelę
        panel.add(buttonDisable); // Pridedame atsijungimo mygtuką į panelę
        frame.add(panel, BorderLayout.SOUTH); // Pridedame panelę į langą apačioje
        frame.pack(); // Automatiškai sureguliuojame lango dydį pagal turinį
        frame.setLocationRelativeTo(null); // Nustatome langą centro pozicijoje
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Nesileidžia uždaryti lango tiesiogiai
        // Įvykio klausytojas lango uždarymui
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnect()) {
                    client.disableClient();
                }
                System.exit(0);
            }
        });
        frame.setVisible(true);
        buttonDisable.addActionListener(new ActionListener() {   // Įvykio klausytojas atsijungimo mygtukui
            @Override
            public void actionPerformed(ActionEvent e) {
                client.disableClient();
            }
        });
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.connectToServer();
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessageOnServer(textField.getText());
                textField.setText("");
            }
        });
    }

    protected void addMessage(String text) {
        messages.append(text);
    }

    // Metodas atnaujina vartotojų sąrašą
    protected void refreshListUsers(Set<String> listUsers) {
        users.setText("");
        if (client.isConnect()) {
            StringBuilder text = new StringBuilder("A list of users:\n"); // Sukuriame naują tekstą
            for (String user : listUsers) { // Kiekvienam vartotojui
                text.append(user + "\n");
            }
            users.append(text.toString()); // Pridedame tekstą į vartotojų sritį
        }
    }

    // Metodas išveda dialogą serverio adresui gauti
    protected String getServerAddressFromOptionPane() {
        while (true) { // Nuolatinis ciklas, kol gausime validų adresą

            String addressServer = JOptionPane.showInputDialog(
                    frame, "Write server address",
                    "Address server..",
                    JOptionPane.QUESTION_MESSAGE
            );
            return addressServer.trim(); // Gražiname įvestą tekstą su pašalintais tarpais
        }
    }

    // Metodas išveda dialogą serverio portui gauti
    protected int getPortServerFromOptionPane() {
        while (true) { // Nuolatinis ciklas, kol gausime validų portą
            String port = JOptionPane.showInputDialog(
                    frame, "Write port server:",
                    "Port server..",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return Integer.parseInt(port.trim()); // Bando konvertuoti į skaičių
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame, "Incorrect server port entered.Try again.",
                        "Error sever port", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Metodas išveda dialogą vartotojo vardui gauti
    protected String getNameUser() {
        return JOptionPane.showInputDialog(
                frame, "Write username:",
                "Username",
                JOptionPane.QUESTION_MESSAGE // Klausimo tipas
        );
    }

    // Metodas rodo klaidos dialogą su pateiktu tekstu
    protected void errorDialogWindow(String text) {
        JOptionPane.showMessageDialog(
                frame, text,
                "Error", JOptionPane.ERROR_MESSAGE //klaida
        );
    }
}