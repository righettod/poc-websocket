package eu.righettod.pocwebsocket.util;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to manage the access token that have been declared as no more usable (explicit user logout)
 */
public class AccessTokenBlacklistUtils {
    /**
     * Message content send by user that indicate that the access token that come along the message must be blacklisted for further usage
     */
    public static final String MESSAGE_ACCESS_TOKEN_INVALIDATION_FLAG = "INVALIDATE_TOKEN";

    /**
     * Use cache to store blacklisted token hash in order to avoid memory exhaustion and be consistent because token are valid 30 minutes so the item live in cache 60 minutes
     */
    private static final CacheAccess<String, String> TOKEN_CACHE;

    static {
        try {
            TOKEN_CACHE = JCS.getInstance("default");
        } catch (CacheException e) {
            throw new RuntimeException("Cannot init token cache !", e);
        }
    }

    /**
     * Add token into the blacklist
     *
     * @param token Token for which the hash must be added
     * @throws NoSuchAlgorithmException If SHA256 is not available
     */
    public static void addToken(String token) throws NoSuchAlgorithmException {
        if (token != null && !token.trim().isEmpty()) {
            String hashHex = computeHash(token);
            if (TOKEN_CACHE.get(hashHex) == null) {
                TOKEN_CACHE.putSafe(hashHex, hashHex);
            }
        }
    }

    /**
     * Check if a token is present in the blacklist
     *
     * @param token Token for which the presence of the hash must be verified
     * @return TRUE if token is blacklisted
     * @throws NoSuchAlgorithmException If SHA256 is not available
     */
    public static boolean isBlacklisted(String token) throws NoSuchAlgorithmException {
        boolean exists = false;
        if (token != null && !token.trim().isEmpty()) {
            String hashHex = computeHash(token);
            exists = (TOKEN_CACHE.get(hashHex) != null);
        }
        return exists;
    }

    /**
     * Compute the SHA256 hash of a token
     *
     * @param token Token for which the hash must be computed
     * @return The hash encoded in HEX
     * @throws NoSuchAlgorithmException If SHA256 is not available
     */
    private static String computeHash(String token) throws NoSuchAlgorithmException {
        String hashHex = null;
        if (token != null && !token.trim().isEmpty()) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes());
            hashHex = DatatypeConverter.printHexBinary(hash);
        }
        return hashHex;
    }

}
