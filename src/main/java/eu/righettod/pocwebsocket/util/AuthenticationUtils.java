package eu.righettod.pocwebsocket.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utility class to manage the authentication JWT token
 */
public class AuthenticationUtils {

    /**
     * Build a JWT token for a user
     *
     * @param login       User login
     * @param accessLevel Access level of the user
     * @return The Base64 encoded JWT token
     * @throws Exception If any error occur during the issuing
     */
    public static String issueToken(String login, String accessLevel) throws Exception {
        //Issue a JWT token with validity of 30 minutes
        Algorithm algorithm = Algorithm.HMAC256(loadSecret());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 30);
        return JWT.create().withIssuer("WEBSOCKET-SERVER").withSubject(login).withExpiresAt(c.getTime()).withClaim("access_level", accessLevel.trim().toUpperCase(Locale.US)).sign(algorithm);
    }

    /**
     * Verify the validity of the provided JWT token
     *
     * @param token JWT token encoded to verify
     * @return The verified and decoded token with user authentication and authorization (access level) information
     * @throws Exception If any error occur during the token validation
     */
    public static DecodedJWT validateToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(loadSecret());
        JWTVerifier verifier = JWT.require(algorithm).withIssuer("WEBSOCKET-SERVER").build();
        return verifier.verify(token);
    }

    /**
     * Load the JWT secret used to sign token using a byte array for secret storage in order to avoid persistent string in memory
     *
     * @return The secret as byte array
     * @throws IOException If any error occur during the secret loading
     */
    private static byte[] loadSecret() throws IOException {
        return Files.readAllBytes(Paths.get("src", "main", "resources", "jwt-secret.txt"));
    }
}
