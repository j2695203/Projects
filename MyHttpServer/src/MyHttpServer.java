import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class MyHttpServer {
    // VARIABLES /////////////////////
    private ServerSocket serverSocket;

    // CONSTRUCTORS /////////////////////
    public MyHttpServer() throws IOException {


            // create server socket
            serverSocket = new ServerSocket(8080);  // create http server with certain port
            while (true) {

                // create client socket ( not web socket )
                Socket clientSocket = serverSocket.accept(); // new client socket every single time

                // create thread
                ConnectionHandler ch = new ConnectionHandler(clientSocket); // implements runnable
                Thread thread = new Thread(ch);
                thread.start();

            }

    }

}
