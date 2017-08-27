package eu.righettod.pocwebsocket.vo;

/**
 * Value object containing information about the result of an authentication operation
 */
public class AuthenticationResponse {
    private boolean isSuccess;
    private String token;
    private String message;

    /**
     * Constructor
     *
     * @param isSuccess Flag indicating if the authentication succeed
     * @param token     Access token encoded in base64
     * @param message   Potential error message
     */
    public AuthenticationResponse(boolean isSuccess, String token, String message) {
        //Affect values
        this.isSuccess = isSuccess;
        this.token = token;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
