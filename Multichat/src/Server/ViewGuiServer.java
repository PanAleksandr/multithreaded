package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewGuiServer {
    private JFrame frame = new JFrame("Service launch");
    private JTextArea dialogWindow = new JTextArea(20, 40); // Sritis serverio dialogams
    private JButton buttonStartServer = new JButton("Start the service");
    private JButton buttonStopServer = new JButton("Stop service");
    private JPanel panelButtons = new JPanel(); // Panelė mygtukams
    private final Server server; // Nuoroda į serverio objektą

    // Konstruktor su nuoroda į serverį
    public ViewGuiServer(Server server) {
        this.server = server;
    }

    // Metodas GUI pradžiai
    protected void initFrameServer() {
        dialogWindow.setEditable(false);
        dialogWindow.setLineWrap(true);
        frame.add(new JScrollPane(dialogWindow), BorderLayout.CENTER);
        panelButtons.add(buttonStartServer);
        panelButtons.add(buttonStopServer);
        frame.add(panelButtons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stopServer();
                System.exit(0);
            }
        });
        frame.setVisible(true);

        buttonStartServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = getPortFromOptionPane();
                server.startServer(port);
            }
        });
        buttonStopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();
            }
        });
    }

    // Metodas, pridedantis naują pranešimą į teksto sritį
    public void refreshDialogWindowServer(String serviceMessage) {
        dialogWindow.append(serviceMessage);
    }

    // Metodas, pateikiantis dialogą serverio prievadui gauti
    protected int getPortFromOptionPane() {
        while (true) {
            String port = JOptionPane.showInputDialog(  // Dialogo langas
                    frame, "Write service port:",
                    "Port service..",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return Integer.parseInt(port.trim()); // Konvertuoja tekstą į skaičių
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame, "Not correct port.Try again",
                        "Error port", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}