package domain;

/**
 * Enumeration of the different event types
 *
 * @author Chachulski, Mathea
 */
public enum EventType {


    PURCHASE("Item Purchased"),
    ADD_NEW_TO_STORAGE("New Item Added to Inventory"),
    INCREASE_ITEM_SUPPLY("Item Supply Increase");

    private final String description;

    EventType(String description){

        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}