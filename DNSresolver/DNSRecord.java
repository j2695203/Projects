import java.io.*;

import java.util.*;

public class DNSRecord {

    // member variables
    String[] rName; // domain name
    short rType; // (16 bit)
    short rClass; // (16 bit)
    int TTL; // (32 bit)
    short rdLength; // (16 bit)
    byte[] rData; // if type is A, then it's IP address
    byte[] restRecord; // authority records & additional records

    // created time of this response
    Date createTime;

    static DNSRecord decodeRecord(InputStream inputStream, DNSMessage dnsMessage) throws IOException {
        // create a DNSRecord
        DNSRecord dnsRecord = new DNSRecord();
        DataInputStream dis = new DataInputStream(inputStream);

        // read domain name and check if it's compressed
        dis.mark(2);
        short firstTwoByte = dis.readShort();

        if( (firstTwoByte & 0xC000) == 0xC000 ){
            dnsRecord.rName = dnsMessage.readDomainName((firstTwoByte & 0x3FFF));
        }else{
            dis.reset();
            dnsRecord.rName = dnsMessage.readDomainName(inputStream);
        }

        // read type
        dnsRecord.rType = dis.readShort();
        dnsRecord.rClass = dis.readShort();
        dnsRecord.TTL = dis.readInt();
        dnsRecord.rdLength = dis.readShort();
        dnsRecord.rData = dis.readNBytes(dnsRecord.rdLength);

        // read authority & additional
        dnsRecord.restRecord = dis.readAllBytes();

        // add creation date of this response
        dnsRecord.createTime = new Date();

        return dnsRecord;
    }


    void writeBytes(ByteArrayOutputStream bOutputStream, HashMap<String, Integer> domainNameLocations) throws IOException {
        // wrap in dataOutputStream
        DataOutputStream dOutputStream = new DataOutputStream(bOutputStream);

        // write qName
        DNSMessage.writeDomainName(bOutputStream,domainNameLocations,rName);
        // write other variables
        dOutputStream.writeShort(rType);
        dOutputStream.writeShort(rClass);
        dOutputStream.writeInt(TTL);
        dOutputStream.writeShort(rdLength);
        dOutputStream.write(rData);
        dOutputStream.write(restRecord);
    }

    @Override
    public String toString() {
        return "DNSRecord{" +
                "rName=" + Arrays.toString(rName) +
                ", rType=" + rType +
                ", rClass=" + rClass +
                ", TTL=" + TTL +
                ", rdLength=" + rdLength +
                ", rData=" + Arrays.toString(rData) +
                ", restRecord=" + Arrays.toString(restRecord) +
                '}';
    }

    /**
     * return whether the creation date + the time to live is after the current time. The Date and Calendar classes will be useful for this.
     * @return
     */
    boolean isExpired(){
        Date currentTime = new Date();
        return ( currentTime.getTime() - createTime.getTime() ) > (TTL * 1000L);
    }

}
