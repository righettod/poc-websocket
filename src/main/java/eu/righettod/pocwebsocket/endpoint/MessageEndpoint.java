package eu.righettod.pocwebsocket.endpoint;

import eu.righettod.pocwebsocket.configurator.EndpointConfigurator;
import eu.righettod.pocwebsocket.decoder.MessageRequestDecoder;
import eu.righettod.pocwebsocket.encoder.MessageResponseEncoder;
import eu.righettod.pocwebsocket.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Class in charge of managing the client messages.
 *
 * @see "http://docs.oracle.com/javaee/7/api/javax/websocket/server/ServerEndpointConfig.Configurator.html"
 * @see "http://svn.apache.org/viewvc/tomcat/trunk/webapps/examples/WEB-INF/classes/websocket/"
 */
@ServerEndpoint(value = "/msg", configurator = EndpointConfigurator.class, subprotocols = {"message"}, encoders = {MessageResponseEncoder.class}, decoders = {MessageRequestDecoder.class})
public class MessageEndpoint {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MessageEndpoint.class);

    /**
     * Handle the beginning of an exchange
     *
     * @param session Exchange session information
     */
    @OnOpen
    public void start(Session session) {
        //Define connection idle timeout and message limits in order to mitigate as much as possible DOS attacks using massive connection opening or massive big messages sending
        int msgMaxSize = 1024 * 1024;//1 MB
        session.setMaxIdleTimeout(60000 * 5);//5 minutes
        session.setMaxTextMessageBufferSize(msgMaxSize);
        session.setMaxBinaryMessageBufferSize(msgMaxSize);
        //Log exchange start
        LOG.info("[MessageEndpoint] Session {} started", session.getId());
        //Affect a new message handler instance in order to process the exchange
        session.addMessageHandler(new MessageHandler(session.getBasicRemote()));
        LOG.info("[MessageEndpoint] Session {} message handler affected for processing", session.getId());
    }

    /**
     * Handle error case
     *
     * @param session Exchange session information
     * @param thr     Error details
     */
    @OnError
    public void onError(Session session, Throwable thr) {
        LOG.error("[MessageEndpoint] Error occur in session {}", session.getId(), thr);
    }

    /**
     * Handle close event
     *
     * @param session     Exchange session information
     * @param closeReason Exchange closing reason
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOG.info("[MessageEndpoint] Session {} closed: {}", session.getId(), closeReason.getReasonPhrase());
    }

}
