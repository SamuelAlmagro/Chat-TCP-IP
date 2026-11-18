package common;

    public enum MessageType {
        CHAT,         // mensaje normal de chat
        JOIN,         // unirse a una sala
        LEAVE,        // salir de una sala
        QUIT,         // desconectarse del servidor
        LIST,         // listar salas y jugadores
        WHO,          // listar jugadores en sala actual
        SYSTEM,       // mensaje del sistema al cliente
        SET_NAME      // establecer nombre de usuario
    }
