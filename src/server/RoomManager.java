package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager {
    private final Map<String, GameRoom> rooms= new HashMap<>();

    //si existe da sala si no crea la sala
    public synchronized GameRoom getOrCreate(String roomName){
        return rooms.computeIfAbsent(roomName, GameRoom::new);
    }

    //elimina sala vacia
    public synchronized void removeIfEmpty(GameRoom room){
        if(room.isEmpty()){
            rooms.remove(room.getName());
            System.out.println("[System] sala eliminada:"+room.getName());
        }
    }

    //existe room¿?
    public synchronized boolean exists(String roomName){return rooms.containsKey(roomName);}

    //listar salas
    public synchronized List<GameRoom> getAllRooms(){return new ArrayList<>(rooms.values());}
}
