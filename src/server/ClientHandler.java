package server;

import common.Message;
import common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.StringJoiner;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final RoomManager roomManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String playerName="Anon";
    private GameRoom currentRoom = null;

    //constructor
    public ClientHandler(Socket socket,RoomManager roomManager){
        this.socket=socket;
        this.roomManager=roomManager;
    }

    //arranque conexion para un unico usuario
    @Override
    public void run(){
        try{
            //establece la conexion
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            sendSystem("Bienvenido al chat de juego. Usa /join <sala> para entrar.");

            Message msg;
            while ((msg=(Message) in.readObject())!=null){handle(msg);}
        }catch(IOException | ClassNotFoundException e){
            System.out.println("[System] Cliente desconectado: "+playerName);
        }finally {
            cleanup();
        }
    }


    //logica comandos
    private void handle(Message msg){
        switch (msg.getType()){
            case SET_NAME:
                playerName=msg.getContent().trim();
                sendSystem("nombre establecido"+playerName);
                break;
            case JOIN:
                handleJoin(msg.getContent().trim());
                break;
            case LEAVE:
                handleLeave();
                break;
            case CHAT:
                handleChat(msg);
                break;
            case LIST:
                handleList();
                break;
            case WHO:
                handleWho();
                break;
            case QUIT:
                cleanup();
                break;
            default:
                sendSystem("comando desconocido");

        }
    }

    //USER unirse a sala
    private void handleJoin(String roomName){
        if(roomName.isEmpty()){
            sendSystem("Uso: /join <nombre sala>");
            return;
        }
        //salir de sala actual si se esta en una
        if(currentRoom != null){
            currentRoom.removeMember(this);
            roomManager.removeIfEmpty(currentRoom);
        }
        currentRoom=roomManager.getOrCreate(roomName);
        currentRoom.addMember(this);
        sendSystem("Has entrado en [" + roomName + "]. Hay " + currentRoom.size() + " jugador(es).");
    }

    //USER abandonar sala
    private void handleLeave(){
        if(currentRoom==null){
            sendSystem("no estas en ninguna sala.");
            return;
        }
        String roomName = currentRoom.getName();
        currentRoom.removeMember(this);
        roomManager.removeIfEmpty(currentRoom);
        currentRoom=null;
        sendSystem("Has salido de ["+ roomName +"].");
    }

    //USER mandar mensaje
    private void handleChat(Message msg){
        if(currentRoom==null){
            sendSystem("Únete a una sala primero con /join <sala>");
            return;
        }
        Message broadcast=new Message(
                MessageType.CHAT,
                playerName,
                msg.getContent(),
                currentRoom.getName()
        );
        currentRoom.broadcast(broadcast, this);
    }

    //lista rooms
    private void handleList() {
        List<GameRoom> rooms = roomManager.getAllRooms();
        if (rooms.isEmpty()) {
            sendSystem("No hay salas activas.");
            return;
        }
        StringBuilder sb = new StringBuilder("Salas activas:\n");
        for (GameRoom room : rooms) {
            sb.append("  [").append(room.getName()).append("] — ")
                    .append(room.size()).append(" jugador(es): ")
                    .append(String.join(", ", room.getMemberNames()))
                    .append("\n");
        }
        sendSystem(sb.toString().trim());
    }

    //listar nombres de la gente en la room actual
    private void handleWho(){
        if(currentRoom==null){
            sendSystem("No estas en ninguna sala.");
            return;
        }
        List<String> names=currentRoom.getMemberNames();
        sendSystem("jugadores en ["+currentRoom.getName()+"]: "+ String.join(", ", names));
    }

//utilidades
public synchronized void sendMessage(Message msg){
        try{
            out.writeObject(msg);
            out.flush();
            out.reset();
        }catch (IOException e) {
            System.out.println("[Server] Error enviado a" + playerName + ": " + e.getMessage());
        }
    }

private void sendSystem(String text){
        sendMessage(new Message(MessageType.SYSTEM, "Servidor", text));
}

private void cleanup(){
        try{
            if(currentRoom != null){
                currentRoom.removeMember(this);
                roomManager.removeIfEmpty(currentRoom);
                currentRoom = null;
            }
            socket.close();
        }catch (IOException e){
            //ignorar al cerrar
        }
    }

public String getPlayerName(){
        return playerName;
}

}
