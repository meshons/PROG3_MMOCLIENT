package game;

public class User extends Player{
    private String secret;
    public User(String s, short id, short x, short y, String secret_) {
        super(s,id,x,y);
        secret=secret_;
    }

    public String getSecret() {
        return secret;
    }
}
