package eu.righettod.pocwebsocket.vo;

import java.util.List;

/**
 * Value object containing information about the result of an operation on a message
 */
public class MessageResponse {
    private boolean isSuccess;
    private List<String> messages;
    private String errorMessage;

    /**
     * Constructor
     *
     * @param isSuccess    Flag indicating if the exchange succeed
     * @param messages     List of user messages
     * @param errorMessage Potential error or warning message
     */
    public MessageResponse(boolean isSuccess, List<String> messages, String errorMessage) {
        this.isSuccess = isSuccess;
        this.messages = messages;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
