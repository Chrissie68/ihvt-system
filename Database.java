import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.sql.ResultSet;

public class Database {
    static String url = "jdbc:mysql://localhost:3306/Nerdygadgets";
    static String username = "root";
    static String password = "";

    public static void executeChangeQuery(String query) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(query);
            System.out.println(rowsAffected + " row(s) affected.");
        } catch (SQLException e) {
            e.printStackTrace();
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
}
