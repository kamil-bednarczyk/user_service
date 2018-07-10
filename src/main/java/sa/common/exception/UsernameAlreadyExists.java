package sa.common.exception;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String username) {
        super("Username: " + username + " already exists in database");
    }
}
