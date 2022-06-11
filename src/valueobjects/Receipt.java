package valueobjects;

import domain.SessionState;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Blueprint for a receipt.
 *
 * @author Chachulski, Mathea
 */
public class Receipt {

        private int customerNumber = 0;
        private String customerName = "";
        private Address customerAddress = null;
        private LocalDate now = LocalDate.now();
        private DateTimeFormatter df = ofPattern("dd.MM.yyyy");

        private String purchaseDate = "";

        private String cartItemsAsString = "";

        public Receipt(int customerNumber, String cartItemsAsString, Address customerAddress){
                this.customerNumber = customerNumber;
                this.cartItemsAsString = cartItemsAsString;
                this.customerName = SessionState.currentUser.name;
                this.customerAddress = customerAddress;
                this.purchaseDate = df.format(now);
        }

        public int getCustomerNumber(){
                return this.customerNumber;
        }

        public String getCustomerName(){
                return this.customerName;
        }

        public Address getCustomerAddress(){
                return this.customerAddress;
        }

        public String getCartItemsAsString(){
                return this.cartItemsAsString;
        }

        public String getPurchaseDate(){
                return this.purchaseDate;
        }

}
