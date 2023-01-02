import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

public class HTTPResponse {
    private Socket client_;
    private String filename_;
    private HashMap headers_;
    private boolean isWsResponse = false;

    HTTPResponse(Socket clientSocket, String filename, HashMap headers ){
        client_ = clientSocket;
        filename_ = filename;
        headers_ = headers;
    }

    public void doit() throws IOException, InterruptedException, NoSuchAlgorithmException {
        //open the request file ('filename')
        if (filename_.equals("/")){
            filename_ = "index.html";
        }
        filename_ = "resources/" + filename_;

        String result;
        File file = new File(filename_);
        try{
            if(file.exists()){
                result = "200 OK";
            }else{
                throw new FileNotFoundException();
            }
        }catch (FileNotFoundException e){
            result = "404 not found";
        }


        OutputStream outputstream = null;
        try {
            outputstream = client_.getOutputStream();

        } catch (IOException e) {
            System.out.println("Client Output Stream fail");
            throw new RuntimeException(e);
        }

        PrintWriter pw = new PrintWriter(outputstream);

        // send the response header (HTTP)
        if( !headers_.containsKey("Sec-WebSocket-Key") ){

            String typeName = filename_.split("\\.")[1];
            pw.println("HTTP/1.1 " + result);
            pw.println("Content-type: text/" + typeName );
            pw.println("Content-Length:" + file.length() );
            pw.print("\n");

            pw.flush();

            // send the data from file (with delay to test threads)
            FileInputStream fileInputStream = new FileInputStream(filename_);
            for( int i = 0; i < file.length(); i++ ) {
                pw.write( fileInputStream.read() );
                pw.flush();
                Thread.sleep( 0 ); // Maybe add <- if images are still loading too quickly...
            }

//        // send the data (original way)
//        Path filepath = Paths.get(path);
//        String content = null;
//        try {
//            content = Files.readString(filepath);
//        } catch (IOException e) {
//            System.out.println("Unable to find file");
//            throw new RuntimeException(e);
//        }
//        pw.println(content);
//
//        pw.flush();
//        pw.close();

        }else{

            // Deal with Web Socket, get response key accept
            String encodedKey = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((headers_.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));

            // print response header
            pw.println("HTTP/1.1 101 Switching Protocols");
            pw.println("Upgrade: websocket");
            pw.println("Connection: Upgrade");
            pw.println("Sec-WebSocket-Accept: " + encodedKey );
            pw.print("\n");

            pw.flush();

            System.out.println("HTTP/1.1 101 Switching Protocols");
            System.out.println("Upgrade: websocket");
            System.out.println("Connection: Upgrade");
            System.out.println("Sec-WebSocket-Accept: " + encodedKey );
            isWsResponse = true;

        }

    }

    public boolean getIsWsResponse(){
        return isWsResponse;
    }
}


