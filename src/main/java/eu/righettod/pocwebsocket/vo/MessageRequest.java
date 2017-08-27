package eu.righettod.pocwebsocket.vo;

/**
 * Value object containing information sent by the client for an operation on message
 */
public class MessageRequest {
    private String token;
    private String content;

    /**
     * Constructor
     *
     * @param token   Access token
     * @param content Message content
     */
    public MessageRequest(String token, String content) {
        this.token = token;
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public String getContent() {
        return content;
    }
}
