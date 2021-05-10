package sy.iyad.server.Utils;


public class Users {

    private final int password;
    private final String profile;
    private final String username;

    public Users(String username, int password, String profile) {
        this.password = password;
        this.profile = profile;
        this.username = username;
    }

    public int getPassword() {
        return this.password;
    }

    public String getProfile() {
        return this.profile;
    }

    public String getUsername() {
        return this.username;
    }

}
