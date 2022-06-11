package domain.Exceptions;

public class NotEnoughItemInStockException extends Exception{
    public NotEnoughItemInStockException(){
        super("-- There's not enough item in Stock! --");
    }
}
