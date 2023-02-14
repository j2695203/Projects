import java.io.*;
import java.lang.reflect.Array;
import java.net.IDN;

public class DNSHeader {
    // variables
    short ID; // (16 bit) ex. 0x4e62
    boolean QR; // (1 bit) message is a request (0=false), or a response (1=true).
    byte OPCODE; // (4 bit) kind of query in this message.(0~2) You should use 0, representing a standard query.
    boolean AA; // (1 bit) Authoritative Answer - the server is authoritative (1=true) (0=false)
    boolean TC; // (1 bit) TrunCation - message was truncated.
    boolean RD; // (1 bit) RD Recursion Desired - directs the name server to pursue the query recursively. You should use 1, representing that you desire recursion.
    boolean RA; // (1 bit) Recursion Available - this be is set or cleared in a response, and denotes whether recursive query support is available in the name server. Recursive query support is optional.
    byte Z; // (3 bit) Reserved for future use. You must set this field to 0.
    byte Rcode; // (4 bit) Response code - part of responses. (0~5) set 0 here
    short QDcount; // (16 bits integer) number of entries in the question section.
    short ANcount; // (16 bits integer) number of resource records in the answer section.
    short NScount; // (16 bits integer) number of name server resource records in the authority records section. You should set this field to 0, and should ignore any response entries in this section.
    short ARcount; // (16 bits integer) number of resource records in the additional records section. You should set this field to 0, and should ignore any response entries in this section.

    /**
     * Read the header from an input stream
     * @param inputStream
     * @return DNSHeader
     * @throws IOException
     */
    static DNSHeader decodeHeader(InputStream inputStream) throws IOException {

        // create a header
        DNSHeader header = new DNSHeader();
        // use DataInputStream
        DataInputStream dis = new DataInputStream(inputStream);

        // read 2 bytes (ID)
        header.ID = dis.readShort();

        // read 3rd byte (QR| Opcode |AA|TC|RD)
        byte thirdByte = dis.readByte();
        header.QR = (thirdByte>>7) != 0;
        header.OPCODE = (byte) ((thirdByte>>3) & 0xF);
        header.AA = ((thirdByte>>2) & 0x1) != 0;
        header.TC = ((thirdByte>>1) & 0x1) != 0;
        header.RD = ((thirdByte) & 0x1) != 0;

        // read 4th byte (RA| Z | RCODE)
        byte fourthByte = dis.readByte();
        header.RA = (fourthByte>>7) != 0;
        header.Z = (byte) ((fourthByte>>4) & 0x7); // old version has 3 bits // 010 may be 2
        header.Rcode = (byte) (fourthByte & 0xF);

        // read short (QDcount, ANcount, NScount, ARcount)
        header.QDcount = dis.readShort();
        header.ANcount = dis.readShort();
        header.NScount = dis.readShort();
        header.ARcount = dis.readShort();

        return header;
    }

    /**
     * Create the header for the response. It will copy some fields from the request
     * @param request
     * @param response
     * @return DNSHeader for response
     */
    static DNSHeader buildHeaderForResponse(DNSMessage request, DNSMessage response){

        DNSHeader rHeader = new DNSHeader();
        // copy header's value from request
        rHeader = request.dnsHeader;
        // switch some values
        rHeader.QR = true;
        rHeader.RA = true;
        rHeader.ANcount = 1;

        return rHeader;
    }

    /**
     * encode the header to bytes to be sent back to the client.
     * @param outputStream
     * @throws IOException
     */
    void writeBytes(OutputStream outputStream) throws IOException {

        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        byte thirdByteOut = 0;
        if(QR) thirdByteOut = (byte) (thirdByteOut|0x80);
        thirdByteOut = (byte) (thirdByteOut|(OPCODE<<3));
        if(AA) thirdByteOut = (byte) (thirdByteOut|0x04);
        if(TC) thirdByteOut = (byte) (thirdByteOut|0x02);
        if(RD) thirdByteOut = (byte) (thirdByteOut|0x01);

        byte fourthByteOut = 0;
        if(RA) fourthByteOut = (byte) (fourthByteOut|0x80);
        fourthByteOut = (byte) (fourthByteOut|(Z<<4));
        fourthByteOut = (byte) (fourthByteOut|Rcode);

        dataOutputStream.writeShort(ID);
        dataOutputStream.writeByte(thirdByteOut);
        dataOutputStream.writeByte(fourthByteOut);
        dataOutputStream.writeShort(QDcount);
        dataOutputStream.writeShort(ANcount);
        dataOutputStream.writeShort(NScount);
        dataOutputStream.writeShort(ARcount);

    }

    /**
     * @return human-readable string version of a header object
     */
    @Override
    public String toString() {
        return "DNSHeader{" +
                "ID=" +  Integer.toHexString(ID & 0xFFFF) +
                ", QR=" + (QR?1:0) +
                ", OPCODE=" + Integer.toHexString(OPCODE & 0xFF) +
                ", AA=" + (AA?1:0) +
                ", TC=" + (TC?1:0) +
                ", RD=" + (RD?1:0) +
                ", RA=" + (RA?1:0) +
                ", Z=" + Integer.toHexString(Z & 0x7) +
                ", Rcode=" + Integer.toHexString(Rcode & 0xF) +
                ", QDcount=" + QDcount +
                ", ANcount=" + ANcount +
                ", NScount=" + NScount +
                ", ARcount=" + ARcount +
                '}';
    }

}
