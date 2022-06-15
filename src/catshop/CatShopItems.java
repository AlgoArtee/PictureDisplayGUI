package catshop;

import domain.Shop;
import valueobjects.Item;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CatShopItems extends JFrame {

    private Shop shop = new Shop("Shop");
    private JPanel contentPanel;
    private JTable itemDisplay;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Throwable e){
            e.printStackTrace();
        }


        EventQueue.invokeLater(() -> {
            try {
                CatShopItems frame = new CatShopItems();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public CatShopItems() throws IOException, ClassNotFoundException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 600);

        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPanel);

        itemDisplay = new JTable();
        itemDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadShopItems();

        contentPanel.add(new JScrollPane(itemDisplay),BorderLayout.CENTER);
        //this.add(contentPanel);


    }

    private void loadShopItems() throws IOException, ClassNotFoundException {

        Map<Integer, Item> itemList = shop.getInventory();

        DefaultTableModel dtf = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        dtf.addColumn("Id");
        dtf.addColumn("Name");
        dtf.addColumn("Photo");
        dtf.addColumn("Availability");
        dtf.addColumn("Price");
        dtf.addColumn("Description");
        dtf.addColumn("In Stock");
        dtf.addColumn("Bulk Size");

        for (Map.Entry<Integer, Item> entry : itemList.entrySet()){

            dtf.addRow(new Object[] {
                    entry.getKey(),
                    entry.getValue().getItemName(),
                    entry.getValue().getPic(),
                    entry.getValue().getIsAvailable(),
                    entry.getValue().getItemPrice(),
                    entry.getValue().getDescription(),
                    entry.getValue().getAmountInStock(),
                    entry.getValue().getBulkSize()
            });

        }



        itemDisplay.setModel(dtf);
        //itemDisplay.setRowHeight(0,100);
        itemDisplay.setRowHeight(70);
        itemDisplay.getTableHeader().setReorderingAllowed(false);
        itemDisplay.getColumnModel().getColumn(2).setCellRenderer(new ImageRenderer());
    }

    private class ImageRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int colum){
            String photoName = value.toString();
            ImageIcon imageIcon = new ImageIcon(new ImageIcon("src/itemresources/"+photoName).
                    getImage().getScaledInstance(60,60, Image.SCALE_DEFAULT));
            return new JLabel(imageIcon);
        }
    }

}