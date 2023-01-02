import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ConnectionHandler implements Runnable{
    private Socket clientSocket_;
    ConnectionHandler( Socket clientSocket){
        clientSocket_ = clientSocket;
    }
    @Override
    public void run() {
        String roomName = null;

        // HTTP REQUEST
        HTTPRequest request = new HTTPRequest (clientSocket_);
        request.doit();

        String filename = request.getFilename();
        HashMap headers = request.getHeaders();

        // HTTP RESPONSE
        HTTPResponse response = new HTTPResponse(clientSocket_, filename, headers );
        try {
            response.doit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // DEAL WITH WEB SOCKET
        if( request.isWsRequest() ){
            while (true) {

                String decodedString;
                try {

                    // READ WS (encoded) REQUEST

                    DataInputStream wsRequest = new DataInputStream(clientSocket_.getInputStream());

                    // step 1: check if masked
                    boolean isMasked = false;
                    byte[] twoBytes = new byte[2];
                    twoBytes = wsRequest.readNBytes(2);
                    if ((twoBytes[1] & 0x80) != 0) {
                        isMasked = true;
                    }
                    System.out.println("\nisMasked: " + isMasked );

                    // step 2: check payload length
                    int payloadLength = 0;
                    if ((twoBytes[1] & 0x7F) <= 125) {
                        payloadLength = (twoBytes[1] & 0x7F);
                    } else if ((twoBytes[1] & 0x7F) == 126) {
                        payloadLength = wsRequest.readShort();
                    } else if ((twoBytes[1] & 0x7F) == 127) {
                        payloadLength = (int) wsRequest.readLong();
                    }
                    System.out.println("length:" + (twoBytes[1] & 0x7F));

                    // step 3: check masking key if isMasked
                    byte[] maskingKey = new byte[4];
                    if (isMasked) {
                        maskingKey = wsRequest.readNBytes(4);
                    }
                    System.out.println("maskingKey: " + maskingKey[0] + " " + maskingKey[1] + " " + maskingKey[2] + " " + maskingKey[3]);

                    // step 4: read payload data based on length, and decode it
                    byte[] encodedData;
                    byte[] decodedData = new byte[payloadLength];
                    encodedData = wsRequest.readNBytes(payloadLength);
                    for (int i = 0; i < encodedData.length; i++) {
                        decodedData[i] = (byte) (encodedData[i] ^ maskingKey[i % 4]);
                    }
                    decodedString = new String(decodedData, StandardCharsets.UTF_8);
                    System.out.println( "decode: " + decodedString);


                    // SEND RESPONSE TO WSs (all clients)

                    // create json object
                    String firstRequestString = decodedString.split(" ")[0];
                    String userName = "";
                    JSONObject jsonObject = new JSONObject();
                    Room clientRoom = null;

                    // save in json based on request type

                    // if it's "join/leave" request
                    if( firstRequestString.equals("join") || firstRequestString.equals("leave") ){
                        // save in json
                        roomName = decodedString.split(" ")[2];
                        userName = decodedString.split(" ")[1];
                        jsonObject.put("type", firstRequestString );
                        jsonObject.put("room", roomName );
                        jsonObject.put("user", userName );

                        // get a new/exist room
                        clientRoom = Room.getRoom(roomName);

                        // add/remove the clientSocket in this room
                        if( firstRequestString.equals("join") ){
                            clientRoom.addClient( clientSocket_ );
                        }else{
                            clientRoom.removeClient( clientSocket_ );
                        }

                        // send msg to all WSs in the room ( someone join/leave )
                        clientRoom.sendOldUsersToNewClient( clientSocket_, jsonObject );
                        clientRoom.sendNewMsgToAllClient( jsonObject );

                    // if it's "message" request
                    }else{
                        // find user's room
                        Room emptyRoom = new Room();
                        clientRoom = emptyRoom.getSocketRoom( clientSocket_ );

                        // save in json
//                        String messageToRoom = decodedString.split(" ")[1];
                        String messageToRoom = decodedString.substring( decodedString.indexOf(" ") + 1);
                        jsonObject.put("type", "message" );
                        jsonObject.put("user", firstRequestString );
                        jsonObject.put("room", clientRoom.getRoomName() );
                        jsonObject.put("message", messageToRoom);

                        // send msg to all WSs in the room ( user's input msg )
                        clientRoom.sendNewMsgToAllClient( jsonObject );

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        // DEAL WITH NOT WEB SOCKET
        }else{
            try {
                clientSocket_.close();
            } catch (IOException e) {
                System.out.println("close client socket fail"); // test
                throw new RuntimeException(e);
            }
        }
    }
}
