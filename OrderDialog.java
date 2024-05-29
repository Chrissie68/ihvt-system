import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class OrderDialog extends JDialog implements ActionListener {
    private final JButton AddOrderLine, RemoveOrderLine;
    private JLabel OrderNumber, ProductenLijst;
    private JScrollPane scrollPane;
    private JTable ProductsShow;
    private Object orderID;
    private JFrame frame;
    private JPanel container;
    private Object rowDataOrderLineID;
    Object rowDataStockItemID;
    public OrderDialog(JFrame frame, Boolean modal, Object orderID){
        super(frame, modal);
        setTitle("Order menu");
//        setLayout(new GridLayout(10,3));
        setLayout(new GridLayout(2,1));
        this.frame = frame;
        this.orderID = orderID;
        AddOrderLine = new JButton("Product toevoegen");
        RemoveOrderLine = new JButton("Product verwijderen");

        OrderNumber = new JLabel("Ordernummer: " + orderID);
        ProductenLijst = new JLabel("Producten in de order");
        RemoveOrderLine.addActionListener(this);
        AddOrderLine.addActionListener(this);


        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + orderID.toString() + "'");
            ProductsShow = new JTable(model);
            ProductsShow.getTableHeader().setReorderingAllowed(false);
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
                    rowDataOrderLineID = ProductsShow.getValueAt(row, 0);
                    rowDataStockItemID = ProductsShow.getValueAt(row, 1);
                    try{
                        QtyChangeStockOrderlineDialog Qtychange = new QtyChangeStockOrderlineDialog(frame, true, rowDataOrderLineID, rowDataStockItemID);
                        if(Qtychange.getDoneCheck()){
                            try {
                                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + orderID + "'");
                                ProductsShow.setModel(model);
                            } catch (SQLException a) {
                                a.printStackTrace();
                            }
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException a){
                        System.out.println("Er is een probleem");
                        a.printStackTrace();
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
                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + orderID.toString() + "'");
                ProductsShow = new JTable(model);
                ProductsShow.getTableHeader().setReorderingAllowed(false);
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
            AddProductDialog addproduct = new AddProductDialog(frame, true, orderID);
            if(addproduct.getCheckDone()){
                try {
                    DefaultTableModel model = Database.executeSelectQuery("SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + orderID.toString() + "'");
                    ProductsShow = new JTable(model);
                    ProductsShow.getTableHeader().setReorderingAllowed(false);
                    this.remove(scrollPane);
                    this.scrollPane = new JScrollPane(ProductsShow);
                    add(scrollPane);
                    revalidate();
                } catch (SQLException a) {
                    a.printStackTrace();
                }
            }
        }
    }
}
