package eu.righettod.pocwebsocket.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to manage the list of message
 */
public class MessageUtils {

    /** Simple message stored indexed by user login*/
    public static final ConcurrentHashMap<String, List<String>> MESSAGES_DB = new ConcurrentHashMap<>();

}
