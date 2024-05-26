import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDialog extends JDialog implements ActionListener {
    private JButton AddOrderLine, RemoveOrderLine, ExecuteOrderLine;
    private JLabel OrderNumber, ProductenLijst;
    JScrollPane scrollPane;
    JTable ProductsShow;
    Object order;
    JFrame frame;
    JPanel container;
    Object rowDataOrderLineID;
    Object rowDataStockItemID;
    int BoxSize = 20;
    String OrderQuery = "SELECT S.StockItemName, o.OrderLineID, o.StockItemID, o.Quantity, s.Size FROM orderlines o JOIN stockitems s ON o.StockItemID = s.StockItemID WHERE o.OrderID =";
    String DeleteQuery = "DELETE FROM orderlines WHERE OrderLineID =";

    public OrderDialog(JFrame frame, Boolean modal, Object order) {
        super(frame, modal);
        setTitle("Order menu");
        setLayout(new GridLayout(2, 1));
        this.frame = frame;
        this.order = order;
        AddOrderLine = new JButton("Product toevoegen");
        RemoveOrderLine = new JButton("Product verwijderen");
        ExecuteOrderLine = new JButton("Uitvoeren order");

        OrderNumber = new JLabel("Ordernummer: " + order);
        ProductenLijst = new JLabel("Producten in order");
        RemoveOrderLine.addActionListener(this);
        AddOrderLine.addActionListener(this);
        ExecuteOrderLine.addActionListener(this);

        try {
            DefaultTableModel model = Database.executeSelectQuery(OrderQuery + order.toString());
            ProductsShow = new JTable(model);
            scrollPane = new JScrollPane(ProductsShow);
            scrollPane.setPreferredSize(new Dimension(1000, 200));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        container = new JPanel(new GridLayout(10, 3));
        container.add(OrderNumber);
        container.add(AddOrderLine);
        container.add(RemoveOrderLine);
        container.add(ExecuteOrderLine);
        container.add(ProductenLijst);


        ProductsShow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int row = ProductsShow.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    rowDataOrderLineID = ProductsShow.getValueAt(row, 0);
                    rowDataStockItemID = ProductsShow.getValueAt(row, 1);
                    try {
                        QtyChangeStockOrderlineDialog Qtychange = new QtyChangeStockOrderlineDialog(frame, true, rowDataOrderLineID, rowDataStockItemID);
                        if (Qtychange.doneCheck) {
                            try {
                                DefaultTableModel model = Database.executeSelectQuery(OrderQuery + order);
                                ProductsShow.setModel(model);
                            } catch (SQLException a) {
                                a.printStackTrace();
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException a) {
                        System.out.println("Het probleem is verholpen");
                    }
                }
            }
        });

        add(container);
        add(scrollPane);
        ProductsShow.setDefaultEditor(Object.class, null);
        setSize(1000, 500);
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == RemoveOrderLine) {
            try {
                int row = ProductsShow.getSelectedRow();
                Object OrderLineIdGet = ProductsShow.getValueAt(row, 0);
                Database.executeChangeQuery(DeleteQuery + OrderLineIdGet);
                DefaultTableModel model = Database.executeSelectQuery(OrderQuery+ order.toString());
                ProductsShow = new JTable(model);
                this.remove(scrollPane);
                revalidate();
                this.scrollPane = new JScrollPane(ProductsShow);
                add(scrollPane);
                revalidate();
            } catch (Exception a) {
                a.printStackTrace();
            }
        }
        if (e.getSource() == AddOrderLine) {
            AddProduct addproduct = new AddProduct(frame, true, order);
            if (addproduct.checkDone) {
                try {
                    DefaultTableModel model = Database.executeSelectQuery(OrderQuery + order.toString());
                    ProductsShow = new JTable(model);
                    this.remove(scrollPane);
                    this.scrollPane = new JScrollPane(ProductsShow);
                    add(scrollPane);
                    revalidate();
                    System.out.println(ProductsShow.getValueAt(1, 1));
                } catch (SQLException a) {
                    a.printStackTrace();
                }
            }
        }
        if (e.getSource() == ExecuteOrderLine) {
            executeOrder();
        }
    }

    private void executeOrder() {
        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT o.OrderLineID, s.StockItemName, o.StockItemID, s.Size, o.Quantity FROM orderlines o JOIN stockitems s ON o.StockItemID = s.StockItemID WHERE o.OrderID = '" + order.toString() + "'");
            List<Object[]> orderLines = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                Object[] row = new Object[model.getColumnCount()];
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row[j] = model.getValueAt(i, j);
                }
                orderLines.add(row);
            }

            List<List<Object[]>> boxes = firstFitAlgorithm(orderLines);

            if (boxes != null) {
                new OrderVisualizationDialog(order, frame, true, boxes);
            } else {
                System.out.println("Sorteren gefaald, artikelen hebben ongeldige maat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<List<Object[]>> firstFitAlgorithm(List<Object[]> orderLines) {
        List<List<Object[]>> boxes = new ArrayList<>();
        try {
            for (Object[] orderLine : orderLines) {
                double size = Double.parseDouble(orderLine[3].toString());
                boolean placed = false;
                for (List<Object[]> box : boxes) {
                    double currentBoxSize = box.stream().mapToDouble(line -> Double.parseDouble(line[2].toString())).sum();
                    if (currentBoxSize + size <= BoxSize) {
                        box.add(orderLine);
                        placed = true;
                        break;
                    }
                }
                if (!placed) {
                    List<Object[]> newBox = new ArrayList<>();
                    newBox.add(orderLine);
                    boxes.add(newBox);
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return boxes;
    }
}
