package server;

import common.Message;
import common.MessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRoom {
    private final String name;
    //personas en la sala de chat (cada persona en el chat tiene un client handler propio)
    private final List<ClientHandler> members = new ArrayList<>();

    //contructor
    public GameRoom(String name){
        this.name=name;
    }

    //get
    public String getName() {return name;}

    //metodo añadir persona al chat
    public synchronized void addMember(ClientHandler client){
        members.add(client);
        broadcast(new Message(
                MessageType.SYSTEM,
                "Servidor",
                client.getPlayerName()+"se ha unido a ["+name+"]",
                name
        ),null);
    }

    //sala vacia¿?
    public synchronized boolean isEmpty(){return members.isEmpty();}

    //cuanta gente en sala¿?
    public synchronized int size(){return members.size();}

    //listar gente en sala
    public synchronized List<String> getMemberNames(){
        List<String> names=new ArrayList<>();
        for(ClientHandler c : members){
            names.add(c.getPayerName());
        }
        return Collections.unmodifiableList(names);
    }

    //metodo eliminar a una persona del chat
    public synchronized void removeMember(ClientHandler client){
        members.remove(client);
        if(!members.isEmpty()){
            broadcast(new Message(
                    MessageType.SYSTEM,
                    "Servidor",
                    client.getPlayerName()+"ha abandonado ["+name+"]",
                    name
            ),null);
        }
    }

    //emitir avisos
    public synchronized void broadcast(Message msg, ClientHandler exclude) {
        for (ClientHandler member : members) {
            if (member != exclude) {
                member.sendMessage(msg);
            }
        }
    }

}
