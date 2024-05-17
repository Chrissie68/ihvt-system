import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class OrderDialog extends JDialog implements ActionListener {
    private JButton AddOrderLine, RemoveOrderLine, ChangeQty;
    private JLabel OrderNumber, ProductenLijst;
    JScrollPane scrollPane;
    JTable ProductsShow;
    Object order;
    JFrame frame;
    public OrderDialog(JFrame frame, Boolean modal, Object order){
        super(frame, modal);
        setTitle("Order menu");
        setLayout(new GridLayout(10,3));
        this.frame = frame;
        this.order = order;
        AddOrderLine = new JButton("Product toevoegen");
        RemoveOrderLine = new JButton("Product verwijderen");
        ChangeQty = new JButton("Aanpassen hoeveelheid");
        OrderNumber = new JLabel("Ordernummer: " + order);
        ProductenLijst = new JLabel("Producten in de order");
        RemoveOrderLine.addActionListener(this);
        AddOrderLine.addActionListener(this);
        ChangeQty.addActionListener(this);

        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + order.toString() + "'");
            ProductsShow = new JTable(model);
            scrollPane = new JScrollPane(ProductsShow);
            System.out.println(ProductsShow.getValueAt(1,1));

        } catch (SQLException e) {
            e.printStackTrace();
        }






        add(OrderNumber);
        add(AddOrderLine);
        add(RemoveOrderLine);
        add(ChangeQty);
        add(ProductenLijst);
        add(scrollPane);

//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == ChangeQty){
            try{
                int row = ProductsShow.getSelectedRow();
                System.out.println(ProductsShow.getValueAt(row, 0));
                QtyChangeDialog Qtychange = new QtyChangeDialog(frame, true, ProductsShow.getValueAt(row, 0), ProductsShow);
                if(Qtychange.doneCheck){
                    try {
                        DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + order.toString() + "'");
                        ProductsShow = new JTable(model);
                        JScrollPane newScrollPane = new JScrollPane(ProductsShow);
                        this.remove(scrollPane);
                        add(newScrollPane);
                        revalidate();
                        System.out.println(ProductsShow.getValueAt(1,1));
                    } catch (SQLException a) {
                        a.printStackTrace();
                    }
                }
            }
            catch(ArrayIndexOutOfBoundsException a){
                System.out.println("Het probleem is verholpen");
            }
        }
        if(e.getSource() == RemoveOrderLine){
            try{
                int row = ProductsShow.getSelectedRow();
                Object OrderLineIdGet = ProductsShow.getValueAt(row, 0);
                String query = "DELETE FROM orderlines WHERE OrderLineID = '" + OrderLineIdGet + "'";
                Database.executeChangeQuery(query);
                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + order.toString() + "'");
                ProductsShow = new JTable(model);
                this.remove(scrollPane);
                revalidate();
                this.scrollPane = new JScrollPane(ProductsShow);
                add(scrollPane);
                revalidate();
            }
            catch(Exception a){
                a.printStackTrace();
            }
        }
        if(e.getSource() == AddOrderLine){
            AddProduct addproduct = new AddProduct(frame, true, order);
            if(addproduct.checkDone){
                try {
                    DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + order.toString() + "'");
                    ProductsShow = new JTable(model);
                    this.remove(scrollPane);
                    this.scrollPane = new JScrollPane(ProductsShow);
                    add(scrollPane);
                    revalidate();
                    System.out.println(ProductsShow.getValueAt(1,1));
                } catch (SQLException a) {
                    a.printStackTrace();
                }
            }
        }
    }
}
