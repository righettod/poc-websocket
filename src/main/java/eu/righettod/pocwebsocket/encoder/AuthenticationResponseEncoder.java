package eu.righettod.pocwebsocket.encoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import eu.righettod.pocwebsocket.vo.AuthenticationResponse;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.File;
import java.io.IOException;

/**
 * Encode AuthenticationResponse object to JSON text representation.
 * <p>
 * As there one instance of the encoder class by endpoint session so we can use the JsonSchema as encoder instance variable.
 */
public class AuthenticationResponseEncoder implements Encoder.Text<AuthenticationResponse> {

    /**
     * JSON validation schema associated to this type of message
     */
    private JsonSchema validationSchema = null;

    /**
     * Initialize encoder and associated JSON validation schema
     *
     * @throws IOException If any error occur during the object creation
     * @throws ProcessingException If any error occur during the schema loading
     */
    public AuthenticationResponseEncoder() throws IOException, ProcessingException {
        JsonNode node = JsonLoader.fromFile(new File("src/main/resources/authentication-response-schema.json"));
        this.validationSchema = JsonSchemaFactory.byDefault().getJsonSchema(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(AuthenticationResponse object) throws EncodeException {
        //Generate the JSON representation
        String json = new Gson().toJson(object);
        try {
            //Validate the generated representation against the dedicated schema
            //Use validation mode with report in order to enable further inspection/tracing of the error details
            //Moreover the validation method "validInstance()" generate a NullPointerException if the representation do not respect the expected schema
            //so it's more proper to use the validation method with report
            ProcessingReport validationReport = this.validationSchema.validate(JsonLoader.fromString(json), true);
            //Ensure there no error
            if (!validationReport.isSuccess()) {
                //Simply reject the message here: Don't care about error details...
                throw new EncodeException(object, "Validation of the generated representation failed !");
            }
        } catch (IOException | ProcessingException e) {
            throw new EncodeException(object, "Cannot validate the generated representation to a JSON valid representation !", e);
        }

        return json;
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
