import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Database {
    private static final String url = "jdbc:mysql://localhost:3306/Nerdygadgets";
    private static final String username = "root";
    private static final String password = "";

    public static void executeChangeQuery(String query) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (CommunicationsException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DefaultTableModel executeSelectQuery(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        DefaultTableModel model = new DefaultTableModel();
        for (int i = 1; i <= columnCount; i++) {
            model.addColumn(metaData.getColumnName(i));
        }
        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getObject(i);
            }
            model.addRow(row);
        }

        resultSet.close();
        statement.close();
        connection.close();
        return model;
    }

    public static boolean enoughStockCheck(int value, int stockItemID){
        String query = "SELECT QuantityOnHand FROM stockitemholdings WHERE StockItemID = '" + stockItemID + "'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int stock = rs.getInt("QuantityOnHand");
                System.out.println(stock);
                if (stock >= value) {
                    System.out.println("Het werkt");
                }
                return stock >= value;
            } else {
                System.out.println("StockItemID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Er is iets mis gegaan! " + e.getMessage());
        }
        return false;
    }

    public static boolean stockItemIdValid(int stockItemID){
        String query = "SELECT StockItemID FROM stockitems WHERE StockItemID = '" + stockItemID + "'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Er is iets mis gegaan! " + e.getMessage());
        }
        return false;
    }

    public static Object[] informationForInsertOrderLine(int stockItemID) {
        String query = "SELECT StockItemName, UnitPackageID, TaxRate FROM stockitems WHERE StockItemID = '" + stockItemID + "'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && stockItemIdValid(stockItemID)) {
                Object[] informationArray = new Object[3];
                informationArray[0] = rs.getObject("StockItemName");
                informationArray[1] = rs.getObject("UnitPackageID");
                informationArray[2] = rs.getObject("TaxRate");
                return informationArray;
            } else {
                System.out.println("No data found for StockItemID: " + stockItemID);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return null;
    }
    public static void addOrder(){
        DateTimeFormatter dateWithHrs = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String query = "INSERT INTO orders(CustomerID, SalespersonPersonID, ContactPersonID, OrderDate, ExpectedDeliveryDate, IsUndersupplyBackordered, LastEditedBy, LastEditedWhen) VALUES(1, 1, 1, '" + date.format(now) + "', '" + date.format(now) + "', 1, 9, '" + dateWithHrs.format(now) + "')";
        executeChangeQuery(query);
    }

    public static void removeOrder(int orderID){
        String deleteOrderLinesQuery = "DELETE FROM orderlines WHERE OrderID ="+ orderID;
        String deleteOrderQuery = "DELETE FROM orders WHERE OrderID ="+ orderID;
        executeChangeQuery(deleteOrderLinesQuery);
        executeChangeQuery(deleteOrderQuery);
    }
    public static String lastOrderID(){
        String query = "SELECT max(OrderID) FROM orders";
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
                return rs.getString("max(OrderID)");
            }
        }
        catch (SQLException n) {
            throw new RuntimeException(n);
        }
        return null;
    }

    public static boolean databaseCheck(){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT 1");
            return true;

        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
