package domain.Exceptions;

public class PasswordNotExistendException extends Exception {
    public PasswordNotExistendException(){
        super("-- Wrong Password! --");
    }
}
