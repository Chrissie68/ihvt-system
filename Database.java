

import java.sql.*;
public class Database extends GOOEY {

    public static void loadResults() {
        try {
            String url = "jdbc:mysql://localhost:3306/Nerdygadgets";
            String username = "root";
            String password = "";

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC LIMIT 5");


            StringBuilder resultText = new StringBuilder("<html><body>");
            resultText.append("Orderlijst").append("<br>");
            while (resultSet.next()) {
                int LastOrderID = resultSet.getInt(1);
                int LastcustomerID = resultSet.getInt(2);
                resultText.append("Order ID: ").append(LastOrderID).append(", Customer ID: ").append(LastcustomerID).append("<br>");

            }

            resultText.append("</body></html>");
            resultLabel.setText(resultText.toString());

            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }

}
