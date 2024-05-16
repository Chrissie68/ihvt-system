import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Database {
    static String url = "jdbc:mysql://localhost:3306/Nerdygadgets";
    static String username = "HMI";  //Was root
    static String password = "HMItest"; //Was leeg

    public static void executeChangeQuery(String query) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            // Execute the update query
            int rowsAffected = statement.executeUpdate(query);
            System.out.println(rowsAffected + " row(s) affected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DefaultTableModel executeQuery(String query) throws SQLException {
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
}
