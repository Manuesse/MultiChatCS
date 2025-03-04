import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class MulticastServer {
    public static void main(String[] args) throws IOException {
        byte[] bufferOUT;
        int porta = 6789;
        InetAddress gruppo = InetAddress.getByName("225.4.5.6");
        // Crea un socket multicast per inviare messaggi al gruppo
        MulticastSocket socket = new MulticastSocket();
        
        System.out.println("SERVER AVVIATO. Premere Ctrl+C per chiudere.");
        
        // Ciclo infinito per inviare periodicamente un messaggio dal server
        while (true) {
            // Prepara un messaggio che inizia con \"SERVER:\" seguito dalla data e ora correnti
            String dString = "SERVER: " + new Date().toString();
            bufferOUT = dString.getBytes();
            DatagramPacket packet = new DatagramPacket(bufferOUT, bufferOUT.length, gruppo, porta);
            socket.send(packet);
            System.out.println(dString);
            try {
                // Aspetta 1 secondo prima di inviare il messaggio successivo
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}