package eu.righettod.pocwebsocket.decoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import eu.righettod.pocwebsocket.vo.MessageRequest;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.File;
import java.io.IOException;

/**
 * Decode JSON text representation to an MessageRequest object
 * <p>
 * As there one instance of the decoder class by endpoint session so we can use the JsonSchema as decoder instance variable.
 */
public class MessageRequestDecoder implements Decoder.Text<MessageRequest> {

    /**
     * JSON validation schema associated to this type of message
     */
    private JsonSchema validationSchema = null;

    /**
     * Initialize decoder and associated JSON validation schema
     *
     * @throws IOException         If any error occur during the object creation
     * @throws ProcessingException If any error occur during the schema loading
     */
    public MessageRequestDecoder() throws IOException, ProcessingException {
        JsonNode node = JsonLoader.fromFile(new File("src/main/resources/message-request-schema.json"));
        this.validationSchema = JsonSchemaFactory.byDefault().getJsonSchema(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRequest decode(String s) throws DecodeException {
        try {
            //Validate the provided representation against the dedicated schema
            //Use validation mode with report in order to enable further inspection/tracing of the error details
            //Moreover the validation method "validInstance()" generate a NullPointerException if the representation do not respect the expected schema
            //so it's more proper to use the validation method with report
            ProcessingReport validationReport = this.validationSchema.validate(JsonLoader.fromString(s), true);
            //Ensure there no error
            if (!validationReport.isSuccess()) {
                //Simply reject the message here: Don't care about error details...
                throw new DecodeException(s, "Validation of the provided representation failed !");
            }
        } catch (IOException | ProcessingException e) {
            throw new DecodeException(s, "Cannot validate the provided representation to a JSON valid representation !", e);
        }

        return new Gson().fromJson(s, MessageRequest.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean willDecode(String s) {
        boolean canDecode = false;

        //If the provided JSON representation is empty/null then we indicate that representation cannot be decoded to our expected object
        if (s == null || s.trim().isEmpty()) {
            return canDecode;
        }

        //Try to cast the provided JSON representation to our object to validate at least the structure (content validation is done during decoding)
        try {
            MessageRequest test = new Gson().fromJson(s, MessageRequest.class);
            canDecode = (test != null);
        } catch (Exception e) {
            //Ignore explicitly any casting error...
        }

        return canDecode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(EndpointConfig config) {
        //Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        //Not used
    }
}
