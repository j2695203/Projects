import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Room {
    // VARIABLES
    private String roomName_;
    static ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Socket> clientSockets = new ArrayList<>();
    ArrayList<JSONObject> responseJoinLeaveJsons = new ArrayList<>();

    // CONSTRUCTOR
    private Room( String roomName ){
        roomName_ = roomName;
    }
    public Room(){}

    // FUNCTIONS
    public synchronized static Room getRoom(String roomName){
        // If room already exists, return it.
        if( rooms.size() > 0 ){
            for( Room el: rooms ) {
                if (el.getRoomName().equals(roomName)) {
                    return el;
                }
            }
        }
        // Otherwise create the room, add it to the list of rooms, and return the new room.
        Room roomNew = new Room(roomName);
        rooms.add(roomNew);
        return roomNew;
    }

    public synchronized void addClient( Socket clientSocket ){
        clientSockets.add( clientSocket );
    }
    public synchronized void removeClient( Socket clientSocket ){
        clientSockets.remove( clientSocket );
    }

    public synchronized void sendOldUsersToNewClient(Socket clientSocket, JSONObject jsonObject ) throws IOException {
        if(jsonObject.get("type").equals("join")){
            OutputStream outputStream = clientSocket.getOutputStream();
            for( JSONObject el: responseJoinLeaveJsons){
                responseWs(outputStream, el);
            }
        }
        // save responseJson to json array
        responseJoinLeaveJsons.add(jsonObject);
    }

    public synchronized void sendNewMsgToAllClient(JSONObject jsonObject ) throws IOException {

        // response this jsonObject to all clients in this room
        for( Socket el: clientSockets ){
            OutputStream outputStream = el.getOutputStream();
            responseWs( outputStream, jsonObject);
        }
    }
    public String getRoomName(){
        return roomName_;
    }

    public Room getSocketRoom( Socket clientSocket ){
        for( Room room: rooms){
            for( Socket socket: room.clientSockets ){
                if( socket == clientSocket ){
                    return room;
                }
            }
        }
        return null;
    }

    private void responseWs( OutputStream outputStream, JSONObject jsonObject ) throws IOException {
        // step 1: FIN + opcode
        outputStream.write( (byte) 0x81 );

        // step 2: mask(0) + payload length
        byte[] payloadData = jsonObject.toString().getBytes();
        System.out.println("JSONString:" + jsonObject.toJSONString()); // test

        if( payloadData.length <= 125 ){
            outputStream.write( (byte)(payloadData.length & 0x7F) );
        }else if( (payloadData.length >= 126) && payloadData.length < Math.pow(2,16)){
            outputStream.write( (byte)0x7E );
            outputStream.write( (byte) ( (payloadData.length >> 8) & 0xFF) );
            outputStream.write( (byte) (payloadData.length & 0xFF) );
        }else if( payloadData.length >= Math.pow(2,16) ){
            outputStream.write( (byte)0x7F );
            for( int i = 7; i >= 0; i-- ){
                outputStream.write( (byte) ( (payloadData.length >> (8*i) ) & 0xFF) );
            }
        }
        // step 3: payload data
        outputStream.write(payloadData);
        outputStream.flush();
    }

}
