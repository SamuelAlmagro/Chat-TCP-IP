package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    private static final int PORT = 5555;

    public static void main(String[] args){
        RoomManager roomManager = new RoomManager();
        System.out.println("[Server] Escuchando en puerto"+ PORT + "...");

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] Nueva conexion: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, roomManager);
                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.start();
            }
        }catch (IOException e){
            System.err.println("[Server] Error fatal" + e.getMessage());
        }
    }
}
