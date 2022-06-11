package valueobjects;

import domain.ItemManager;
import domain.SessionState;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class represents a cart which handels webshop items inside a CartList.
 * It belongs to a specific customer and is invoked at customer login.
 * Customers can add items they want to buy, increase the amount of items, buy them and clear their cart.
 *
 * @author Chachulski, Mathea
 */

public class Cart implements Serializable {
    private Map<Integer, Integer> cartList = new HashMap<>();
    private ItemManager itemManager = new ItemManager();
    private int customerNumber = 0;

    public Map<String, String> itemNameQuantity = new HashMap<>();

    public Cart(int customerNumber) throws IOException, ClassNotFoundException {
        this.customerNumber = customerNumber;
    }

    public Map<Integer, Integer> getCartList() {
        return this.cartList;
    }

    public void increaseItemAmount(int itemNumber, int increaseBy) {
        this.cartList.put(itemNumber, increaseBy);
    }

    private String calculateTotal() throws IOException, ClassNotFoundException {
        double total = 0;
        String totalAsString = "";
        try {
            java.text.DecimalFormat format = new java.text.DecimalFormat("#.00");
            for (int itemNumberInCart : this.cartList.keySet()) {
                for (int itemNumberInStock : this.itemManager.getItemList().keySet()) {
                    if (itemNumberInCart == itemNumberInStock) {
                        total += this.cartList.get(itemNumberInCart) * this.itemManager.getItemList().get(itemNumberInStock).getItemPrice();
                    }
                }
            }
            return totalAsString = format.format(total);
        } catch (IOException ioe) {
            throw new IOException();
        } catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    /**
     * Returns the quantity of a particular item inside the cart, the items name
     * and price per item.
     */
    public String getCartItemsAsString() throws IOException, ClassNotFoundException {
        StringBuilder itemsInCart = new StringBuilder();
        if (this.cartList.isEmpty()) {
            return " \n Cart is empty";
        }
        try {
            for (int itemNumberInCart : this.cartList.keySet()) {
                for (int itemNumberInStock : this.itemManager.getItemList().keySet()) {
                    if (itemNumberInCart == itemNumberInStock) {
                        if (itemNameQuantity.containsKey(itemNumberInCart)){
                        }else{
                            itemNameQuantity.put(Integer.toString(this.cartList.get(itemNumberInCart)),this.itemManager.getItemList().get(itemNumberInStock).getItemName());
                        }
                        itemsInCart.append(this.itemManager.getItemList().get(itemNumberInStock).toString())
                                .append(" - quantity: ").append(cartList.get(itemNumberInCart))
                                .append("\n");
                    }
                }
            }
            return itemsInCart.append(" \n").append("Price in total: ").append(calculateTotal()).append("\n").toString();
        } catch (IOException ioe) {
            throw new IOException();
        } catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public void clearCart() {
        cartList.clear();
    }

    public Receipt buyNow() throws IOException, ClassNotFoundException {
        try {
            Receipt receipt = new Receipt(customerNumber, getCartItemsAsString(), getCustomerAddress());
            clearCart();
            return receipt;
        }catch(IOException ioe){
            throw new IOException();
        }catch(ClassNotFoundException cnfe){
            throw new ClassNotFoundException();
        }
    }

    /**
     * This method casts the current user back into a customer object in order to access its address.
     *
     */
    public Address getCustomerAddress(){
        Customer currentCustomer = (Customer) SessionState.currentUser;
        return currentCustomer.getAddress();
    }


    public Map<String, String> getItemNameQuantity(){
        return getItemNameQuantity();
    }
}
