import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

public class DNSServer {
    DNSServer() throws IOException {

        // Step 1 : Create a socket to listen at port 8053
        DatagramSocket dSocket = new DatagramSocket(8053); // carry the packet to the destination, receive it whenever the server sends any data
        DatagramPacket dPacket_from_user = null; // packet for sending/receiving data via a datagramSocket
        byte[] receive = new byte[128];


        while (true)
        {
            // Step 2 : Receive request from the client

            // create a DatagramPacket
            dPacket_from_user = new DatagramPacket(receive, receive.length);
            // receive the data in byte buffer.
            dSocket.receive(dPacket_from_user);
            // decode message from user
            DNSMessage msg_from_user = DNSMessage.decodeMessage(dPacket_from_user.getData());


            // Step 3 : Respond to the client
            
            DNSMessage msg_send_user;

            for(DNSQuestion q: msg_from_user.dnsQuestions){

                // If there is a valid answer in cache, add that the response
                if(DNSCache.inCache(q)){

                    DNSRecord[] answers = new DNSRecord[1]; // (just for casting)
                    answers[0] = DNSCache.cacheMap.get(q);
                    // build response
                    msg_send_user = DNSMessage.buildResponse(msg_from_user, answers);
                    System.out.println( "response from cache: " + Arrays.toString(msg_send_user.toBytes()));

                }else{
                    // forward the request Google (8.8.8.8)
                    InetAddress ip = InetAddress.getByName("8.8.8.8");
                    DatagramPacket dPacket_send_google = new DatagramPacket(dPacket_from_user.getData(), dPacket_from_user.getData().length, ip, 53);
                    dSocket.send(dPacket_send_google);

                    // got response from google
                    byte[] receive_from_google = new byte[128];
                    DatagramPacket dPacket_from_google = new DatagramPacket(receive_from_google, receive_from_google.length);
                    dSocket.receive(dPacket_from_google);
                    System.out.println( "response from google: " + Arrays.toString(dPacket_from_google.getData()));

                    // build response
                    msg_send_user = DNSMessage.buildResponse(msg_from_user, DNSMessage.decodeMessage(dPacket_from_google.getData()).dnsRecords.toArray(new DNSRecord[0]));
                    System.out.println( "response from buildR: " + Arrays.toString(msg_send_user.toBytes()));

                    // add to cache if the domain name exists (google's answer RR != 0)
                    if(DNSMessage.decodeMessage(dPacket_from_google.getData()).dnsHeader.ANcount != 0){
                        DNSCache.insertCache(q,DNSMessage.decodeMessage(dPacket_from_google.getData()).dnsRecords.get(0)); // (only store first answer in this project)
                    }
                }

                // send the response back to the client.
                DatagramPacket dPacket_send_user = new DatagramPacket(msg_send_user.toBytes(), msg_send_user.toBytes().length, dPacket_from_user.getAddress(), dPacket_from_user.getPort());
                dSocket.send(dPacket_send_user);
            }
        }
    }
}
