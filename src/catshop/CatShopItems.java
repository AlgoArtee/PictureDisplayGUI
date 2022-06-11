package catshop;

import domain.Shop;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CatShopItems extends JFrame {

    private Shop shop = new Shop("Shop");
    private JPanel contentPane;
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
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        itemDisplay = new JTable();
        contentPane.add(itemDisplay, BorderLayout.CENTER);

    }

}