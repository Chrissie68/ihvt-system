import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Stockcheck extends JDialog implements ActionListener {
    JTable ProductsShow;
    JScrollPane scrollBar;
    Object rowData;
    JFrame frame;
    String selectQuery;
    public Stockcheck(JFrame frame, Boolean modal){
        super(frame, modal);
        setTitle("Voorraadlijst");
        setLayout(new GridLayout(1,1 ));
        this.frame = frame;
        try{
            selectQuery = "SELECT stockitems.StockItemID, StockItemName, QuantityOnHand FROM stockitems INNER JOIN stockitemholdings ON stockitems.StockItemID = stockitemholdings.StockItemID";
            ProductsShow = new JTable(Database.executeSelectQuery(selectQuery));
            scrollBar = new JScrollPane(ProductsShow);
        }
        catch(SQLException a){
            System.out.println("Er is iets fout gegaan in de database");
        }
        ProductsShow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int row = ProductsShow.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    rowData = ProductsShow.getValueAt(row, 0);
                    System.out.println("Double clicked on: "+ rowData);
                    try{
                        QtyChangeStockDialog Qtychange = new QtyChangeStockDialog(frame, true, rowData);
                        if(Qtychange.doneCheck){
                            try {
                                remove(scrollBar);
                                ProductsShow = new JTable(Database.executeSelectQuery(selectQuery));
                                scrollBar = new JScrollPane(ProductsShow);
                                add(scrollBar);
                                revalidate();
                            } catch (SQLException a) {
                                a.printStackTrace();
                            }
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException a){
                        System.out.println("Het probleem is verholpen");
                    }
                }
            }
        });
        add(scrollBar);
        ProductsShow.setDefaultEditor(Object.class, null);
        setSize(1000,500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
