package eu.righettod.pocwebsocket.handler;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import eu.righettod.pocwebsocket.enumeration.AccessLevel;
import eu.righettod.pocwebsocket.util.AccessTokenBlacklistUtils;
import eu.righettod.pocwebsocket.util.AuthenticationUtils;
import eu.righettod.pocwebsocket.util.MessageUtils;
import eu.righettod.pocwebsocket.vo.MessageRequest;
import eu.righettod.pocwebsocket.vo.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle message flow
 */
public class MessageHandler implements javax.websocket.MessageHandler.Whole<MessageRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);

    /**
     * Reference to the communication channel with the client
     */
    private RemoteEndpoint.Basic clientConnection;

    /**
     * Constructor
     *
     * @param clientConnection Reference to the communication channel with the client
     */
    public MessageHandler(RemoteEndpoint.Basic clientConnection) {
        this.clientConnection = clientConnection;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(MessageRequest message) {
        MessageResponse response = null;
        try {
            /*Step 1: Verify the token*/
            String token = message.getToken();
            //Verify if is it in the blacklist
            if (AccessTokenBlacklistUtils.isBlacklisted(token)) {
                throw new IllegalAccessException("Token is in the blacklist !");
            }

            //Verify the signature of the token
            DecodedJWT decodedToken = AuthenticationUtils.validateToken(token);

            /*Step 2: Verify the authorization (access level)*/
            Claim accessLevel = decodedToken.getClaim("access_level");
            if (accessLevel == null || AccessLevel.valueOf(accessLevel.asString()) == null) {
                throw new IllegalAccessException("Token have an invalid access level claim !");
            }

            /*Step 3: Do the expected processing*/
            //Init the list of the messages for the current user
            if (!MessageUtils.MESSAGES_DB.containsKey(decodedToken.getSubject())) {
                MessageUtils.MESSAGES_DB.put(decodedToken.getSubject(), new ArrayList<>());
            }

            //Add message to the list of message of the user if the message is a not a token invalidation order otherwise add the token to the blacklist
            if (AccessTokenBlacklistUtils.MESSAGE_ACCESS_TOKEN_INVALIDATION_FLAG.equalsIgnoreCase(message.getContent().trim())) {
                AccessTokenBlacklistUtils.addToken(message.getToken());
            } else {
                MessageUtils.MESSAGES_DB.get(decodedToken.getSubject()).add(message.getContent());
            }

            //According to the access level of user either return only is message or return all message
            List<String> messages = new ArrayList<>();
            if (accessLevel.asString().equals(AccessLevel.USER.name())) {
                MessageUtils.MESSAGES_DB.get(decodedToken.getSubject()).forEach(s -> messages.add(String.format("(%s): %s", decodedToken.getSubject(), s)));
            } else if (accessLevel.asString().equals(AccessLevel.ADMIN.name())) {
                MessageUtils.MESSAGES_DB.forEach((k, v) -> v.forEach(s -> messages.add(String.format("(%s): %s", k, s))));
            }

            //Build the response object indicating that exchange succeed
            if (AccessTokenBlacklistUtils.MESSAGE_ACCESS_TOKEN_INVALIDATION_FLAG.equalsIgnoreCase(message.getContent().trim())) {
                response = new MessageResponse(true, messages, "Token added to the blacklist");
            }else{
                response = new MessageResponse(true, messages, "");
            }

        } catch (Exception e) {
            LOG.error("[MessageHandler] Error occur in exchange process.", e);
            //Build the response object indicating that exchange fail
            //We send the error detail on client because ware are in POC (it will not the case in a real app)
            response = new MessageResponse(false, new ArrayList<>(), "Error occur during exchange: " + e.getMessage());
        } finally {
            //Send response
            try {
                this.clientConnection.sendObject(response);
            } catch (IOException | EncodeException e) {
                LOG.error("[MessageHandler] Error occur in response object sending.", e);
            }
        }
    }


}
