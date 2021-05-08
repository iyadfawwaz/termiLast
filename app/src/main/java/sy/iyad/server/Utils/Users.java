package sy.iyad.server.Utils;


public class Users {

    private int password;
    private String profile;
    private String username;

    private Users() {
    }

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

    public void setPassword(int password) {
        this.password = password;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
