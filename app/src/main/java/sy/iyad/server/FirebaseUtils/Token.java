package sy.iyad.server.FirebaseUtils;

public class Token {
    private String token;

    private Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
