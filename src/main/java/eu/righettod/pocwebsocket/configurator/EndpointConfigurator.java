package eu.righettod.pocwebsocket.configurator;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.server.ServerEndpointConfig;
import java.util.Arrays;
import java.util.List;

/**
 * Setup handshake rules applied to all WebSocket endpoints of the application.
 * Use to setup the Access Filtering using "Origin" HTTP header as input information.
 *
 * @see "http://docs.oracle.com/javaee/7/api/index.html?javax/websocket/server/ServerEndpointConfig.Configurator.html"
 * @see "https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Origin"
 */
public class EndpointConfigurator extends ServerEndpointConfig.Configurator {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(EndpointConfigurator.class);

    /**
     * Get the expected source origins from a JVM property in order to allow external configuration
     */
    private static final List<String> EXPECTED_ORIGINS =  Arrays.asList(System.getProperty("source.origins").split(";"));

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        boolean isAllowed = EXPECTED_ORIGINS.contains(originHeaderValue);
        String safeOriginValue = Encode.forHtmlContent(originHeaderValue);
        if (isAllowed) {
            LOG.info("[EndpointConfigurator] New handshake request received from {} and was accepted.", safeOriginValue);
        } else {
            LOG.warn("[EndpointConfigurator] New handshake request received from {} and was rejected !", safeOriginValue);
        }
        return isAllowed;
    }

}
