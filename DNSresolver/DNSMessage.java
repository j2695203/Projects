import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DNSMessage {

    // member variables
    byte[] completeMessage;
    DNSHeader dnsHeader;
    ArrayList<DNSQuestion> dnsQuestions = new ArrayList<>();
    ArrayList<DNSRecord> dnsRecords = new ArrayList<>();


    /**
     * Decode message received from socket, store as class member variables.
     * @param bytes packet message received from socket
     * @return the class object DNSMessage
     * @throws IOException for errors in reading from or writing to streams
     */
    static DNSMessage decodeMessage(byte[] bytes) throws IOException {

        // create static message
        DNSMessage dnsMessage = new DNSMessage();

        // step 1: store complete message
        dnsMessage.completeMessage = bytes;

        // step 2: start decode message (3 parts)
        InputStream inputStream = new ByteArrayInputStream(bytes);

        // 2-1: decode header
        dnsMessage.dnsHeader = DNSHeader.decodeHeader(inputStream);

        // 2-2: decode questions
        for( int i = 0; i < dnsMessage.dnsHeader.QDcount; i++ ){
            dnsMessage.dnsQuestions.add( DNSQuestion.decodeQuestion(inputStream, dnsMessage) ) ;
        }

        // 2-3: decode answers and rest records
        for( int i = 0; i < dnsMessage.dnsHeader.ANcount; i++ ){
            dnsMessage.dnsRecords.add( DNSRecord.decodeRecord(inputStream, dnsMessage) );
        }

        return dnsMessage;
    }

    /**
     * Read the pieces of a domain name starting from the current position of the input stream
     * @param inputStream data to be read
     * @return label domain name in string array
     * @throws IOException for errors in reading from or writing to streams
     */
    String[] readDomainName(InputStream inputStream) throws IOException {

        DataInputStream dis = new DataInputStream(inputStream);
        ArrayList<String> domainName = new ArrayList<>();

        byte labelLength = dis.readByte();

        while( labelLength != 0){
            domainName.add(new String(dis.readNBytes(labelLength)));
            labelLength = dis.readByte(); // next label length
        }
        return domainName.toArray(new String[0]);
    }

    /**
     * Read the pieces of a domain name starting from earlier in the message. It's used when there's compression.
     * This method should make a ByteArrayInputStream that starts at the specified byte and call the other version of readDomainName().
     * @param firstByte specified byte position to start reading the domain name in earlier message
     * @return label domain name in string array
     * @throws IOException for errors in reading from or writing to streams
     */
    String[] readDomainName(int firstByte) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(completeMessage, firstByte, completeMessage.length);
        return readDomainName(byteArrayInputStream);
    }

    /**
     *  Join the pieces of a domain name with dots ([ "utah", "edu"] -> "utah.edu" ) (add static)
     * @param pieces domain name labels
     * @return a complete domain name as a string
     */
    static String joinDomainName(String[] pieces){
        String fullName = "";
        for (int i = 0; i < pieces.length; i++){
            if( i == pieces.length - 1){
                fullName += pieces[i];
            }else{
                fullName += pieces[i] + ".";
            }
        }
        return fullName;
    }

    /**
     * Build a response based on the request and the answers that intend to send back.
     * @param request request message part
     * @param answers response message part
     * @return a complete message to response client
     */
    static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers){
        DNSMessage rMessage = new DNSMessage();
        rMessage.dnsHeader = DNSHeader.buildHeaderForResponse(request, rMessage);
        rMessage.dnsQuestions = request.dnsQuestions;
        rMessage.dnsRecords.addAll(List.of(answers));
        return rMessage;
    }

    /**
     * Get the DNSMessage in byte array to put in a packet and send back
     * @return DNSMessage in byte array
     * @throws IOException for errors in reading from or writing to streams
     */
    byte[] toBytes() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HashMap<String,Integer> domainNameLocations = new HashMap<>();

        // header part
        dnsHeader.writeBytes(byteArrayOutputStream);
        // question part
        for (DNSQuestion q:dnsQuestions) {
            q.writeBytes(byteArrayOutputStream,domainNameLocations);
        }
        // record part
        for (DNSRecord r:dnsRecords) {
            r.writeBytes(byteArrayOutputStream,domainNameLocations);
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * If this is the first time we've seen this domain name in the packet, write it using the DNS encoding
     * (each segment of the domain prefixed with its length, 0 at the end), and add it to the hash map.
     * Otherwise, write a back pointer to where the domain has been seen previously.
     *
     * @param bOutputStream output stream
     * @param domainLocations map of domain name we've seen and its location
     * @param domainPieces domain name labels
     * @throws IOException for errors in reading from or writing to streams
     */
    static void writeDomainName(ByteArrayOutputStream bOutputStream, HashMap<String,Integer> domainLocations, String[] domainPieces) throws IOException {
        DataOutputStream dOutputStream = new DataOutputStream(bOutputStream);

        // If we've seen this domain name in the packet before
        if(domainLocations.containsKey(joinDomainName(domainPieces))){
            // decode the compression, and write a back pointer to where the domain has been seen previously
            short compression = (short) 0xC000;
            compression = (short) (compression | domainLocations.get(joinDomainName(domainPieces)));
            dOutputStream.writeShort(compression);

        }else{
            // write it using the DNS encoding
            int pos = bOutputStream.size(); // current position
            for(String el: domainPieces){
                dOutputStream.writeByte(el.length());
                dOutputStream.write(el.getBytes());
            }
            dOutputStream.writeByte(0); // end symbol for domain name
            // add it to the hash map.
            domainLocations.put(joinDomainName(domainPieces), pos);
        }
    }

    @Override
    public String toString() {
        return "DNSMessage{" +
//                "completeMessage=" + Arrays.toString(completeMessage) +
                "dnsHeader=" + dnsHeader +
                ", dnsQuestions=" + dnsQuestions +
                ", dnsRecords=" + dnsRecords +
                '}';
    }

}
