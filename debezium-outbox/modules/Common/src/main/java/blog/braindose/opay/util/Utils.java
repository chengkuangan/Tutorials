package blog.braindose.opay.util;

/**
 * Provides utility methods
 */
public class Utils {
    /**
     * Remove extra double quotes(") and backslash (\) from the JSON string which cause JSON parsing failed.
     * Messages consumed from Kafka seems to have these extra charaters that will render JSON parsing failed.
     * @param payload is the original JSON string from Kafka message
     * @return cleaned JSON string
     */
    static public String cleanJSONString(String payload){
        payload = payload.trim();
        payload = payload.substring(1,payload.length() - 1);
        payload = payload.replace("\\", "");
        return payload;
    }
}
