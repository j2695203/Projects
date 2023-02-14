import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class DNSQuestion {

    // member variables
    String[] qName; // domain name
    short qType; // (16 bit)
    short qClass; // (16 bit)

    /**
     * Read a question from the input stream. Due to compression, it will ask the DNSMessage containing this question to read some fields.
     * @param inputStream
     * @param message
     * @return
     * @throws IOException
     */
    static DNSQuestion decodeQuestion(InputStream inputStream, DNSMessage message) throws IOException {
        // create a question
        DNSQuestion question = new DNSQuestion();
        DataInputStream dis = new DataInputStream(inputStream);

        // read data
        question.qName = message.readDomainName(inputStream);
        question.qType = dis.readShort();
        question.qClass = dis.readShort();

        return question;
    }

    /**
     * Write the question bytes which will be sent to the client. The hash map is used for us to compress the message.
     * @param bOutputStream
     * @param domainNameLocations
     * @throws IOException
     */
    void writeBytes(ByteArrayOutputStream bOutputStream, HashMap<String,Integer> domainNameLocations) throws IOException {

        // wrap in dataOutputStream
        DataOutputStream dOutputStream = new DataOutputStream(bOutputStream);

        // write qName
        DNSMessage.writeDomainName(bOutputStream,domainNameLocations,qName);
        // write qType
        dOutputStream.writeShort(qType);
        // write qClass
        dOutputStream.writeShort(qClass);
    }

    @Override
    public String toString() {
        return "DNSQuestion{" +
                "qName=" + Arrays.toString(qName) +
                ", qType=" + qType +
                ", qClass=" + qClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNSQuestion that = (DNSQuestion) o;
        return qType == that.qType && qClass == that.qClass && Arrays.equals(qName, that.qName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(qType, qClass);
        result = 31 * result + Arrays.hashCode(qName);
        return result;
    }


}
