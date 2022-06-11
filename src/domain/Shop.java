package domain;

import domain.Exceptions.*;
import valueobjects.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for the management of a simpel e-shop.
 * Offers methods for returning all items in stock,
 * for item search, for adding new items
 * and for saving the inventory.
 *
 * @author Chachulski, Mathea
 */

public class Shop {
    private LogInManager logInManager = new LogInManager();
    private ItemManager itemManager;
    private CustomerManager customerManager = new CustomerManager();
    private StaffManager staffManager = new StaffManager();
    private Cart cart;
    public LogEntry entry;
    public Log log = new Log(entry);
    public Set<Map.Entry<String, String>> itemNameQuantity;

    //Ist das nicht besser im ItemManager aufgehoben?
    //private List<Item> inventory = new ArrayList<>();

    public Shop(String shopFile) throws IOException, ClassNotFoundException {
        this.itemManager = new ItemManager();
        this.logInManager.setLogInPhase(LogInPhase.LOGGED_OUT);
        //this.log = new Log(this.entry);
    }

    /**
     * Method returns a copy of the inventory.
     * The original inventory and its copy refer to the same item-objects.
     *
     * @return List of all items in stock (copy)
     */

    public Map<Integer,Item> getInventory() throws IOException, ClassNotFoundException {
        try {
            Map<Integer, Item> inventory = this.itemManager.getItemList();
            return inventory;
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public int getItemNumber(){
        return itemManager.createNextNumber();
    }

    public void logIn(String name, String password) throws PasswordNotExistendException, NameNotExistendException, ClassNotFoundException, IOException {
        try {
            logInManager.processLoginInfo(name, password);
        } catch (PasswordNotExistendException wrongPassword){
            throw new PasswordNotExistendException();
        } catch (NameNotExistendException wrongName){
            throw new NameNotExistendException();
        } catch (IOException e) {
            throw new IOException();
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException();
        }
    }

    public List<String> displayInfo(String itemName){
        return itemManager.displayInfo(itemName);
    }

    public void signUpCustomer(String name, String password, String streetAndNumber, int postalCode, String placeOfResidence) throws IOException, ClassNotFoundException {
        try {
            customerManager.processCustomerSignupInfo(name, password, streetAndNumber, postalCode, placeOfResidence);
        } catch (UserAlreadyExistsException userExists) {
            throw new IOException();
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException();
        }
    }

    public void signUpStaff(String name, String password) throws IOException, ClassNotFoundException {
        try {
            staffManager.processStaffSignupInfo(name, password);
        } catch (UserAlreadyExistsException userExists) {
            throw new IOException();
        } catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public List<Map.Entry<Integer,Item>> listItemsByNumber(){
        return this.itemManager.listItemsByNumber();
    }

    public List<Map.Entry<Integer,Item>> listItemsByName(){
        return this.itemManager.listItemsByName();
    }

    public List<String> searchItem(String itemName) {return this.itemManager.searchItem(itemName);}

    public boolean isLoggedInAs(UserType userType){
        return (getLoginPhase() == LogInPhase.LOGGED_IN && getUserType() == userType);
    }

    public LogInPhase getLoginPhase(){
        return this.logInManager.getLogInPhase();
    }

    public UserType getUserType(){
        return this.logInManager.getUserType();
    }

    public void addItem(String itemName, int number, boolean isAvailable, double price, String description, int inStock) throws ItemAlreadyExistsException {
        Item newItem = new Item(itemName, number, isAvailable, price, description, inStock);

        try {
            this.itemManager.insertItem(number, newItem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logData("addItem", Integer.toString(newItem.getInStock()), newItem.getItemName());

    }

    public List<String> returnLogList(){return this.log.returnLogList();}

    public void addToCart(int itemToAdd, int itemAmount) throws ItemNotExistendException, IOException, ClassNotFoundException, BulkSizeException {
        try {
            if (this.itemManager.getItemList().containsKey(itemToAdd)) {
                if (isBulkItem(itemToAdd)){
                    if (isBulkSizeCorrect(itemToAdd, itemAmount)){
                        this.customerManager.addToCart(itemToAdd, itemAmount);
                    }else {
                        throw new BulkSizeException();
                    }
                }else {
                    this.customerManager.addToCart(itemToAdd, itemAmount);
                }
            } else throw new ItemNotExistendException();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        } catch (BulkSizeException bse) {
            throw new BulkSizeException();
        }
    }

    private boolean isBulkSizeCorrect(int itemToAdd, int itemAmount) throws IOException, ClassNotFoundException {
        try {
            if (this.itemManager.isBulkSizeCorrect(itemToAdd, itemAmount)){
                return true;
            }else {
                return false;
            }
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public void increaseAmountInCart(int itemNumber, int increaseBy){
       this.customerManager.increaseAmountInCart(itemNumber, increaseBy);
    }

    public String getItemsInCartToPrintThem() throws IOException, ClassNotFoundException {
        try {
            return this.customerManager.getItemsInCartToPrintThem();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public void increaseStock(int itemNumber, int increaseBy) throws IOException, ClassNotFoundException {
        this.itemManager.increaseStock(itemNumber, increaseBy);

        // logging increased Stock for an item
        Item loggedItem = itemManager.getItemList().get(itemNumber);
        logData("increaseItemSupply", Integer.toString(loggedItem.getInStock()), loggedItem.getItemName());

        // end logging

        // Update Item List on File with new data
        this.itemManager.persistenceManager.writeItemList(itemManager.getItemList());
    }

    /**
     * This Method filters the inventory for the purchased items (drawn from the CartList of the current user's cart).
     * The amount of purchased items is then read from the cartList. The method uses the items own decreaseInStock()
     * function to subtract that number from the amount of items of this particular type in stock.
     *
     */
    public void decreaseStockAtPurchase() throws NotEnoughItemInStockException, IOException, ClassNotFoundException {

        Map<Integer, Integer> purchasedItems = getCurrentCart().getCartList();
        try {
            for (int itemNumber : purchasedItems.keySet()) {
                Item purchasedItem = getInventory().get(itemNumber);
                for (Item item : getInventory().values()) {
                    if (item.equals(purchasedItem)) {
                        item.decreaseStock(purchasedItems.get(itemNumber));
                    }
                }
            }
        }catch (NotEnoughItemInStockException notEnough){
            throw new NotEnoughItemInStockException();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public String viewCart() throws IOException, ClassNotFoundException {
        try {
            return this.customerManager.viewCart();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public Receipt placeOrder() throws NotEnoughItemInStockException, IOException, ClassNotFoundException {
        try {
            decreaseStockAtPurchase();
            Receipt receipt = getCurrentCart().buyNow();

            //logData Entry
            itemNameQuantity = getCurrentCart().getItemNameQuantity().entrySet();

            for (Map.Entry<String, String> inq: itemNameQuantity) {
                logData("boughtItem", inq.getKey(), inq.getValue());
            }
            //logData Entry End
            return receipt;
        }catch (NotEnoughItemInStockException notEnough){
            throw new NotEnoughItemInStockException();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public void logOut(){
        this.logInManager.logOut();
    }

    protected Cart getCurrentCart(){
        Customer currentCustomer = (Customer) SessionState.currentUser;
        return currentCustomer.getCurrentCart();
    }

    public void clearCart(){
        this.customerManager.clearCart();
    }

    public void logData(String type, String stockAmount, String itemName)
    {
        // ---- Log Data ----



        // Date variable
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        String dateTimeString = now.getDayOfWeek() + " " + now.format(formatter);

        // Item(s) Added
        Map<String,String> itemAdded = new HashMap<>();
        itemAdded.put(stockAmount, itemName);

        // Log Entry
        if(type.equals("addItem"))
        {
            this.entry = new LogEntry(EventType.ADD_NEW_TO_STORAGE.toString(), dateTimeString, itemAdded, SessionState.currentUser.name);
        }
        else if (type.equals("boughtItem")) {

            this.entry = new LogEntry(EventType.PURCHASE.toString(), dateTimeString, itemAdded, SessionState.currentUser.name);
        }
        else if (type.equals("increaseItemSupply")) {
            this.entry = new LogEntry(EventType.INCREASE_ITEM_SUPPLY.toString(), dateTimeString, itemAdded, SessionState.currentUser.name);
        }

        this.log = new Log(this.entry);
        log.writeLogEntries();


        //Log updateLog = new Log(this.entry);
        //updateLog.writeLogEntries();
    }

    public boolean isBulkItem(int itemNumber) throws IOException, ClassNotFoundException {
        try {
            return this.itemManager.isBulkItem(itemNumber);
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public int getBulkSize(int itemToAdd) throws IOException, ClassNotFoundException {
        try {
            return this.itemManager.getBulkSize(itemToAdd);
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

}
