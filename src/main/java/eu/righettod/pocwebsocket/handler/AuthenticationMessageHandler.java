package eu.righettod.pocwebsocket.handler;

import eu.righettod.pocwebsocket.enumeration.AccessLevel;
import eu.righettod.pocwebsocket.util.AuthenticationUtils;
import eu.righettod.pocwebsocket.vo.AuthenticationRequest;
import eu.righettod.pocwebsocket.vo.AuthenticationResponse;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.EncodeException;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import java.io.IOException;

/**
 * Handle authentication message flow
 */
public class AuthenticationMessageHandler implements MessageHandler.Whole<AuthenticationRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationMessageHandler.class);

    /**
     * Reference to the communication channel with the client
     */
    private RemoteEndpoint.Basic clientConnection;

    /**
     * Constructor
     *
     * @param clientConnection Reference to the communication channel with the client
     */
    public AuthenticationMessageHandler(RemoteEndpoint.Basic clientConnection) {
        this.clientConnection = clientConnection;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(AuthenticationRequest message) {
        AuthenticationResponse response = null;
        try {
            //Simulate an authentication
            String authenticationToken = "";
            String accessLevel = this.simulateAuthentication(message.getLogin(), message.getPassword());
            if (accessLevel != null) {
                //Create a simple JSON token representing the authentication profile
                authenticationToken = AuthenticationUtils.issueToken(message.getLogin(), accessLevel);
            }
            //Build the response object
            String safeLoginValue = Encode.forHtmlContent(message.getLogin());
            if (!authenticationToken.isEmpty()) {
                response = new AuthenticationResponse(true, authenticationToken, "Authentication succeed !");
                LOG.info("[AuthenticationMessageHandler] User {} authentication succeed.", safeLoginValue);
            } else {
                response = new AuthenticationResponse(false, authenticationToken, "Authentication failed !");
                LOG.warn("[AuthenticationMessageHandler] User {} authentication failed.", safeLoginValue);
            }
        } catch (Exception e) {
            LOG.error("[AuthenticationMessageHandler] Error occur in authentication process.", e);
            //Build the response object indicating that authentication fail
            response = new AuthenticationResponse(false, "", "Authentication failed !");
        } finally {
            //Send response
            try {
                this.clientConnection.sendObject(response);
            } catch (IOException | EncodeException e) {
                LOG.error("[AuthenticationMessageHandler] Error occur in response object sending.", e);
            }
        }
    }

    /**
     * Simulate an authentication step for our POC context
     *
     * @param login    User login
     * @param password User password
     * @return The access level if the authentication succeed or NULL if the authentication failed
     */
    private String simulateAuthentication(String login, String password) {
        String accessLevel = null;

        if (login == null || login.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return accessLevel;
        }

        //BOB => Normal user
        //ALICE => Admin user
        if ("bob".equalsIgnoreCase(login) && "bob123".equals(password)) {
            accessLevel = AccessLevel.USER.name();
        } else if ("alice".equalsIgnoreCase(login) && "alice123".equals(password)) {
            accessLevel = AccessLevel.ADMIN.name();
        }

        return accessLevel;

    }
}
