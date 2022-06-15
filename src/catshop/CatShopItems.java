package catshop;

import domain.Shop;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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
        setBounds(100, 100, 450, 300);

        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPanel);

        itemDisplay = new JTable();
        //contentPanel.add(itemDisplay, BorderLayout.CENTER);

        loadShopItems();

        contentPanel.add(new JScrollPane(itemDisplay),BorderLayout.CENTER);
        //this.add(contentPanel);


    }

    private void loadShopItems() throws IOException, ClassNotFoundException {

        shop.getInventory();

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

        itemDisplay.setModel(dtf);
    }
}