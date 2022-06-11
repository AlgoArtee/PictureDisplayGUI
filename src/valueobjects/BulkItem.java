package valueobjects;

public class BulkItem extends Item {

    public BulkItem(String itemName, int itemNumber, boolean isAvailable, double price, String description, int inStock, int bulkSize) {
        super(itemName, itemNumber, isAvailable, price, description, inStock);
        this.bulkSize = bulkSize;
    }
}
