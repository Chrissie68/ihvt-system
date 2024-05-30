import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class BPPtoTSPTransform {
    public static int[][] locationTransform(int[] productsIDs) {
        int[][] locationAfter = new int[productsIDs.length][2];
        try {
            // Construct a dynamic query to fetch bin locations for given product IDs
            StringBuilder queryBuilder = new StringBuilder("SELECT StockItemID, BinLocation FROM stockitemholdings WHERE ");
            for (int i = 0; i < productsIDs.length; i++) {
                queryBuilder.append("StockItemID = ").append(productsIDs[i]);
                if (i < productsIDs.length - 1) {
                    queryBuilder.append(" OR ");
                }
            }

            DefaultTableModel model = Database.executeSelectQuery(queryBuilder.toString());
            for (int i = 0; i < model.getRowCount(); i++) {
                String binLocation = model.getValueAt(i, 1).toString();
                char locationChar = binLocation.charAt(0);
                char locationDigit = binLocation.charAt(1);

                switch (locationChar) {
                    case 'A':
                        locationAfter[i][0] = 1;
                        break;
                    case 'B':
                        locationAfter[i][0] = 2;
                        break;
                    case 'C':
                        locationAfter[i][0] = 3;
                        break;
                    case 'D':
                        locationAfter[i][0] = 4;
                        break;
                    case 'E':
                        locationAfter[i][0] = 5;
                        break;
                    default:
                        break;
                }
                locationAfter[i][1] = Character.getNumericValue(locationDigit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locationAfter;
    }
}
