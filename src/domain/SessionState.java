package domain;

import valueobjects.User;

/**
 * Class where the state of the current Session is saved inside of variables.
 *
 * @author Chachulski, Mathea
 */
public class SessionState {
    protected static LogInPhase logInPhase = null;
    protected static UserType userType = null;
    public static User currentUser = null;

    public SessionState(){

    }

}
