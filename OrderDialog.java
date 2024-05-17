import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class OrderDialog extends JDialog implements ActionListener {
    private JButton AddOrderLine, RemoveOrderLine;
    private JLabel OrderNumber, ProductenLijst;
    JScrollPane scrollPane;
    JTable ProductsShow;
    Object order;
    JFrame frame;
    JPanel container;
    Object rowData;
    public OrderDialog(JFrame frame, Boolean modal, Object order){
        super(frame, modal);
        setTitle("Order menu");
//        setLayout(new GridLayout(10,3));
        setLayout(new GridLayout(2,1));
        this.frame = frame;
        this.order = order;
        AddOrderLine = new JButton("Product toevoegen");
        RemoveOrderLine = new JButton("Product verwijderen");

        OrderNumber = new JLabel("Ordernummer: " + order);
        ProductenLijst = new JLabel("Producten in de order");
        RemoveOrderLine.addActionListener(this);
        AddOrderLine.addActionListener(this);


        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + order.toString() + "'");
            ProductsShow = new JTable(model);
            scrollPane = new JScrollPane(ProductsShow);
            scrollPane.setPreferredSize(new Dimension(1000, 200));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        container = new JPanel(new GridLayout(10,3));
        container.add(OrderNumber);
        container.add(AddOrderLine);
        container.add(RemoveOrderLine);
        container.add(ProductenLijst);

        ProductsShow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int row = ProductsShow.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    rowData = ProductsShow.getValueAt(row, 0);
                    System.out.println("Double clicked on: "+ rowData);
                    try{
//                        int row = ProductsShow.getSelectedRow();
//                        System.out.println(ProductsShow.getValueAt(row, 0));
                        QtyChangeOrderlineDialog Qtychange = new QtyChangeOrderlineDialog(frame, true, rowData);
                        if(Qtychange.doneCheck){
                            try {
                                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + order.toString() + "'");
                                ProductsShow = new JTable(model);
                                remove(scrollPane);
                                scrollPane = new JScrollPane(ProductsShow);
                                add(scrollPane);
                                revalidate();
                                System.out.println(rowData);
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

        add(container);
        add(scrollPane);
        ProductsShow.setDefaultEditor(Object.class, null);
        setSize(1000,500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
