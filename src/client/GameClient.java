package client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class GameClient {

    private static final String Host = "localhost";
    private static final int PORT = 5555;

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce tu nombre de jugador: ");
        String playerName = scanner.nextLine().trim();
        if(playerName.isEmpty()) playerName = "Jugador";

        try(Socket socket = new Socket(Host, PORT)){
            //oos
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectOutputStream in = new ObjectOutputStream(socket.getInputStream());

            //hilo receptor segundo plano
            MessageListener listener = new MessageListener(in);
            Thread listenerThread = new Thread(listener);

        }
    }
}
