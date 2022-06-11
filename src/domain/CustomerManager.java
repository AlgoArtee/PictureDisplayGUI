package domain;

import domain.Exceptions.UserAlreadyExistsException;
import valueobjects.Address;
import valueobjects.Cart;
import valueobjects.Customer;
import valueobjects.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import static domain.LogInPhase.LOGGED_IN;

/**
 * Class for the management of webshop customers.
 * Extends UserManager
 * It inherits following methods from its parent class:
 * getUserList(), getLastNumber(), createNextNumber(), checkPassword().
 *
 * @author Chachulski, Mathea
 */
public class CustomerManager extends UserManager implements Serializable {
    public CustomerManager() throws IOException, ClassNotFoundException {
        try {
            addFirstCustomers();
            writeUserListe();
        }catch (IOException ioe){
            throw new IOException();
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException();
        }
    }

    private Address address1(){
        return new Address("Mainstreet 4", 8498, "Duckburg");
    }
    private Address address2(){
        return new Address("Prisonroad 90", 8351, "Duckburg");
    }
    private Address address3(){
        return new Address("Highway To Hell 6", 8199, "Duckburg");
    }

    // zu Testzwecken, solange die Persistenz-Schicht noch nicht aktiv ist
    private void addFirstCustomers() throws IOException, ClassNotFoundException {
        User Minni = new Customer("Minni Mouse", createNextNumber(), "Mickey", address1());
        this.userList.put("Minni Mouse", Minni);
        User Karlo = new Customer("Kater Karlo", createNextNumber(), "Mickey", address2());
        this.userList.put("Kater Karlo", Karlo);
        User Donald = new Customer("Donald Duck", createNextNumber(), "Mickey", address3());
        this.userList.put("Donald Duck", Donald);
    }

    private Map<String, User> readUserList() throws IOException, ClassNotFoundException {
        try {
            return this.persistenceManager.readUserList();
        }catch (IOException ioe){
            throw new IOException();
        }catch (ClassNotFoundException cnfe){
            throw new ClassNotFoundException();
        }
    }

    protected void addCustomer(String name, String password, Address address) throws UserAlreadyExistsException, IOException, ClassNotFoundException {
        User newCustomer = new Customer(name, createNextNumber(), password, address);
        try {
            Map<String, User> currentUserList = this.persistenceManager.readUserList();
            if (!currentUserList.containsKey(name)) {
                currentUserList.put(name, newCustomer);
                this.persistenceManager.writeUserList(currentUserList);
            } else {
                throw new UserAlreadyExistsException(name);
            }
        }catch (IOException ioe){
            throw new IOException();
        }catch (ClassNotFoundException cnfe){
            throw new ClassNotFoundException();
        }
    }

    // This is where the real signup process for customers takes place.
    protected void processCustomerSignupInfo(String newName, String password, String streetAndNumber, int postalCode, String placeOfResidence) throws UserAlreadyExistsException, IOException, ClassNotFoundException {
        Address address = new Address(streetAndNumber, postalCode, placeOfResidence);
        try {
            addCustomer(newName, password, address);
            SessionState.logInPhase = LOGGED_IN;
            SessionState.userType = UserType.CUSTOMER;
        } catch (UserAlreadyExistsException uaee) {
            throw new UserAlreadyExistsException(newName);
        }catch (IOException ioe){
            throw new IOException();
        }catch (ClassNotFoundException cnfe){
            throw new ClassNotFoundException();
        }
    }

    protected void addToCart(int itemToAdd, int itemAmount){

        getCurrentCustomer().addToCart(itemToAdd, itemAmount);
    }


    public void increaseAmountInCart(int itemNumber, int increaseBy){
        getCurrentCustomer().getCurrentCart().increaseItemAmount(itemNumber, increaseBy);
    }

    public String getItemsInCartToPrintThem() throws IOException, ClassNotFoundException {
        try {
            return getCurrentCustomer().getCurrentCart().getCartItemsAsString();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public String viewCart() throws IOException, ClassNotFoundException {
        try {
            return getCurrentCart().getCartItemsAsString();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }
    public void clearCart(){
        getCurrentCart().clearCart();
    }

    private Cart getCurrentCart(){
        return getCurrentCustomer().getCurrentCart();
    }

    private Customer getCurrentCustomer(){
        Customer currentCustomer = (Customer) SessionState.currentUser;
        return currentCustomer;
    }

}
