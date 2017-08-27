package eu.righettod.pocwebsocket.vo;

/**
 * Value object containing information sent by the client for an authentication operation.
 */
    public class AuthenticationRequest {
    private String login;
    private String password;

    /**
     * Constructor
     *
     * @param login    User login
     * @param password User password
     */
    public AuthenticationRequest(String login, String password) {
        this.login = login;
        this.password = password;


    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
