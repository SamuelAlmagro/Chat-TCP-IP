package common;

import java.io.Serializable;

public class Message implements Serializable {

    //identificador serializacion
    private static final long serialVersionUID=1L;

    private final MessageType type;
    private final String sender;
    private final String content;
    private final String room;

    //constructor
    public Message(MessageType type,String sender, String content, String room){
        this.type=type;
        this.sender=sender;
        this.content=content;
        this.room=room;
    }

    //constructor sin sala
    public Message(MessageType type, String sender, String content){
        this(type,sender,content,null);
    }

    //getters
    public MessageType getType() {return type;}
    public String getSender() {return sender;}
    public String getContent() {return content;}
    public String getRoom() {return room;}

    @Override
    public String toString() {
        return "Message{type=" + type + ", sender=" + sender
                + ", room=" + room + ", content=" + content + "}";
    }
}
