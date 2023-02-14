import java.util.HashMap;

public class DNSCache {
    static HashMap<DNSQuestion, DNSRecord> cacheMap = new HashMap<>();

    /**
     * Query if certain domain name is in cache
     * @param question
     * @return whether the domain name in question is in cache
     */
    public static boolean inCache(DNSQuestion question){

        if(cacheMap.containsKey(question)){
            // if it is too old (its TTL has expired), remove it and return "not found."(false)
            if( cacheMap.get(question).isExpired() ){
                cacheMap.remove(question);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Insert records into the cache
     * @param question
     * @param record
     */
    public static void insertCache(DNSQuestion question, DNSRecord record){
        cacheMap.put(question,record);
    }
}
