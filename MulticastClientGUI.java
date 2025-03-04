import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CLIENT CON INTERFACCIA GRAFICA
public class MulticastClientGUI {
    // Componenti grafici
    private static JTextArea textArea;      // Aree di testo per visualizzare i messaggi
    private static JTextField textField;    // Campo per immettere il messaggio da inviare
    // Componenti di rete
    private static MulticastSocket socket;  // Socket multicast
    private static InetAddress group;       // Indirizzo del gruppo multicast
    private static int porta = 6789;        // Porta utilizzata per la comunicazione
    // Nome utente
    private static String username;
    
    public static void main(String[] args) throws Exception {
        // Chiedi il nome utente all'avvio tramite una finestra di dialogo
        username = JOptionPane.showInputDialog(null, "Inserisci il tuo nome:", "Nome Utente", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            // Se non viene inserito un nome valido, termina il programma
            System.out.println("Nome utente non valido. Chiusura del client.");
            System.exit(0);
        }
        
        // Imposta la finestra principale
        JFrame frame = new JFrame("Multicast Client - " + username);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Crea l'area di testo per visualizzare i messaggi
        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        // Crea il pannello inferiore con il campo di testo e il bottone per inviare i messaggi
        JPanel panel = new JPanel(new BorderLayout());
        textField = new JTextField();
        JButton sendButton = new JButton("Invia");
        panel.add(textField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        frame.add(panel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        
        // Creazione del socket multicast
        socket = new MulticastSocket(porta);
        socket.setReuseAddress(true);
        group = InetAddress.getByName("225.4.5.6");
        
        // Unione al gruppo multicast (metodo semplice)
        socket.joinGroup(group);
        
        // Abilita il loopback per ricevere anche i messaggi inviati dallo stesso host
        socket.setLoopbackMode(false);
        
        // Thread per la ricezione dei messaggi dal gruppo multicast
        new Thread(() -> {
            try {
                byte[] bufferIN = new byte[1024];
                while (true) {
                    DatagramPacket packetIN = new DatagramPacket(bufferIN, bufferIN.length);
                    socket.receive(packetIN);
                    // Costruzione della stringa ricevuta
                    String received = new String(packetIN.getData(), 0, packetIN.getLength());
                    // Se il messaggio proviene dal server (inizia con "SERVER:"), lo ignora
                    if (received.startsWith("SERVER:")) {
                        continue;
                    }
                    // Visualizza il messaggio ricevuto nell'area di testo
                    textArea.append(received + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // Gestione dell'azione del bottone "Invia"
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Ottiene il testo dal campo di input
                    String message = textField.getText();
                    if (!message.isEmpty()) {
                        // Prepara il messaggio con il nome utente
                        String userMessage = username + ": " + message;
                        byte[] bufferOUT = userMessage.getBytes();
                        // Crea il pacchetto da inviare al gruppo multicast
                        DatagramPacket packetOUT = new DatagramPacket(bufferOUT, bufferOUT.length, group, porta);
                        socket.send(packetOUT);
                        // Mostra il messaggio inviato nell'area di testo del client
                        // Svuota il campo di testo
                        textField.setText("");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
