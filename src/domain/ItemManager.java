package domain;

import domain.Exceptions.ItemAlreadyExistsException;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;
import valueobjects.BulkItem;
import valueobjects.Item;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for the management of items inside an ItemList Object
 *
 * @author Chachulski, Mathea
 */
public class ItemManager implements Serializable {
    //private ItemList itemListObject = new ItemList();

    //private Map<Integer,Item> itemList = itemListObject.getItems();

    PersistenceManager persistenceManager = new FilePersistenceManager();
    private Map<Integer, Item> itemList = new HashMap<>();

    public ItemManager() throws IOException, ClassNotFoundException {

        //addFirstItems(this.itemList);


        // write Item List to File
        //try {
        //    persistenceManager.writeItemList(itemList);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}


        // intialising itemManager with item list
        try {
            getItemListFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void addFirstItems(Map<Integer, Item> itemList) throws IOException {
        Item item1 = new Item("CAT Scan",1,"catscan_1.png",true,384.9, "No, not the medical procedure! Our cat scanning device will let you know in seconds if your cat has unmet needs, psychological or physiological issues.", 10);
        itemList.put(item1.getItemNumber(), item1);

        Item item2 = new Item("Robo-Cat",2,"robocat_2.jpg",false,5000.99, "Your cutest electronic companion! (Might attack other electronics in your household.)", 4);
        itemList.put(item2.getItemNumber(), item2);

        Item item3 = new Item("Catablet",3,"catablet_3.jpg",true,499.99, "Our AI algorithms can read your cat's wishes by paw vibration and movement on the touch screen. Upgrade your cat-communication experience now, with our new tablet!", 30);
        itemList.put(item3.getItemNumber(), item3);

        Item item4 = new BulkItem("CatPi",4,"catpi_4.png",true,60.50, "The ultimate guide for programming cat-related sensors with Raspberry Pi", 3, 5);
        itemList.put(item4.getItemNumber(), item4);

        Item item5 = new Item("Genome Purrnalysis",5,"GenomePurrnalysis_5.jpg",true,1880.00,"Analyse your cat's genome with this awesome new device!", 112);
        itemList.put(item5.getItemNumber(), item5);

        Item item6 = new BulkItem("NetworCat",6,"networcat_6.jpg",true,250.50, "Ever struggled with getting your cat to accept new cats? Forget weeks of trying to get cats familiar with each other! This device will process each cat's odour, mix it, reproduce it, and spray it in the environment. Your new cat will be accepted within hours!", 27, 2);
        itemList.put(item6.getItemNumber(), item6);










        this.persistenceManager.writeItemList(itemList);
    }
    protected List<String> displayInfo(String itemName) {

        List<String> itemDetails = new ArrayList<>();
        Map<Integer, Item> currentItemList = itemList;

        Map<Integer, Item> filteredMap = currentItemList.entrySet()
                .stream().filter(v -> itemName.equals(v.getValue().getItemName()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        filteredMap.values().forEach(v -> itemDetails.add(v.getItemName()));
        filteredMap.values().forEach(v -> itemDetails.add(v.getDescription()));
        filteredMap.values().forEach(v -> itemDetails.add(v.getItemPrice() + ""));
        filteredMap.values().forEach(v -> itemDetails.add(v.getIsAvailable() + ""));
        filteredMap.values().forEach(v -> itemDetails.add(v.getItemNumber() + ""));

        return itemDetails;
    }


    /**
     * Method to insert an item at the end of the list
     *
     * @param oneItem - the item to be added at the end of the list
     *                //@throws ItemExistsException if item already exists
     */
    public Map<Integer, Item> insertItem(int itemNumber, Item oneItem) throws ItemAlreadyExistsException, IOException { // throws ItemExistsException {
        for (Item item : this.itemList.values()) {
            if (item.getItemName().equals(oneItem.getItemName())) {
                throw new ItemAlreadyExistsException();
            }
        }
        itemList.put(itemNumber, oneItem);
        persistenceManager.writeItemList(itemList);
        return itemList;
    }

    /**
     * Method to delete an item from List
     *
     * @param itemNumber - item to delete
     */
    public void deleteItem(int itemNumber) {

        itemList.entrySet()
                .removeIf(
                        entry -> (itemNumber
                                == entry.getKey()));
    }

    protected void increaseStock(int itemNumber, int increaseBy) {
        this.itemList.get(itemNumber).increaseStock(increaseBy);
    }

    /**
     * Method to search an Item by Item name.
     * The results is a list of Items who returns all Items with the exact search term
     *
     * @param itemName Name of queried item
     * @return List of books with the queried item name (potentially empty)
     */
    protected List<String> searchItem(String itemName) {
        List<String> queryResults = new ArrayList<>();
        Map<Integer, Item> currentItemList = itemList;

        Map<Integer, Item> filteredMap = currentItemList.entrySet()
                //.stream().filter(v-> itemName.equals(v.getValue().getItemName()))
                .stream().filter(v -> v.getValue().getItemName().startsWith(itemName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //System.out.print(currentItemList);

        filteredMap.values().forEach(v -> queryResults.add(v.getItemName()));

        // System.out.print(queryResults);

        return queryResults;
    }


    protected List<Map.Entry<Integer, Item>> listItemsByName() {

        List<Map.Entry<Integer, Item>> l = new ArrayList<>(itemList.entrySet());
        l.sort(Map.Entry.comparingByValue(Comparator.comparing(Item::getItemName)));

        /*
        Collections.sort(l, new Comparator<Map.Entry<Integer, Item>>() {
            @Override
            public int compare(Map.Entry<Integer, Item> o1, Map.Entry<Integer, Item> o2) {
                return o1.getValue().getItemName().compareTo(o2.getValue().getItemName());
            }
        }); */


        return l;
    }

    protected List<Map.Entry<Integer, Item>> listItemsByNumber() {
        List<Map.Entry<Integer, Item>> l1 = new ArrayList<>(itemList.entrySet());
        l1.sort(Map.Entry.comparingByKey());

        return l1;

    }

    protected int getLastNumber() {
        int highestNumber = 0;
        for (Item item : itemList.values()) {
            int itemNumber = item.getItemNumber();
            if (itemNumber > highestNumber) {
                highestNumber = itemNumber;
            }
        }
        return highestNumber;
    }

    protected int createNextNumber() {
        int lastNumber = getLastNumber();
        return lastNumber + 1;
    }

    public void getItemListFromFile() throws IOException, ClassNotFoundException {

        this.itemList = this.persistenceManager.readItemList();
    }

    protected boolean isBulkItem(int itemNumber) throws IOException, ClassNotFoundException {
        if (getItemList().get(itemNumber).getBulkSize() != 0){
            return true;
        }else {
        return false;}
    }

    public boolean isBulkSizeCorrect(int bulkItemToAdd, int itemAmount) throws IOException, ClassNotFoundException {
        try {
            if (itemAmount % getBulkSize(bulkItemToAdd) == 0) {
                return true;
            } else return false;
        }catch (IOException ioe) {
            throw new IOException();
        } catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    protected int getBulkSize(int itemToAdd) throws IOException, ClassNotFoundException {
        try {
            BulkItem bulkItemToAdd = (BulkItem) getItemList().get(itemToAdd);
            return bulkItemToAdd.getBulkSize();
        }catch (IOException ioe) {
            throw new IOException();
        }catch (ClassNotFoundException cnfe) {
            throw new ClassNotFoundException();
        }
    }

    public Map<Integer, Item> getItemList() throws IOException, ClassNotFoundException {
        //Map<Integer, Item> currentItemList = this.persistenceManager.readItemList();
        //return currentItemList;
        return this.itemList;
    }


    /**
     *
     * Method for returning a copy of the item list
     * Security aspect: Users will receive only a copy of the
     *                  original Item List, and will not be able
     *                  to manipulate the original list.
     *
     * @return Liste aller Bücher im Buchbestand (Kopie)
     */
    /*public List<Item> getItemList() {
        return new ArrayList<>(itemList);
    }*/

    // TODO: Change Price on Item, etc
    // ...


    // Persistence Interface for data access / writing
    // private PersistenceManager pm = new FilePersistenceManager();

    /**
     * Method for reading Item data from the Item file
     *
     * @param file file containing the item data / details
     * @throws IOException
     */
   /* public void readItemDataFromFile(String file) throws IOException {
        // Open Persistence Manager for Reading
        pm.openForReading(file);

        Item oneItem;
        do {
            // read Item
            oneItem = pm.loadItem();
            if (oneItem != null) {

                // Insert Item in List
                try {
                    insertItem(oneItem);
                } finally {

                }
            }
        } while (oneItem != null);

        // Persistenz-Schnittstelle wieder schließen
        pm.close();
    } */

    /**
     * Methode zum Schreiben der items in eine Datei.
     *
     * @param datei Datei, in die das inventory geschrieben werden soll
     * @throws IOException
     */
    /* public void writeItemDataToFile(String datei) throws IOException  {
        // Open Persistence Manager for Writing
        pm.openForWriting(datei);

        List<Item> list = itemList;
        int count = 0;
        while (list.size() > count) {
            Item item = list.get(count);
            if (item != null) {
                // Save to File
                pm.saveItem(item);
            }

            count++;
            //list = list.listRestItems();
        }

        // Persistenz-Schnittstelle wieder schließen
        pm.close();
    } */


}