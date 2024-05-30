import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class BPPtoTSPTransform {
    public static int[][] locationTransform(int[] productsIDs) {
        int[][] locationAfter = new int[productsIDs.length][2];
        try {
            //Building the query with inputs from input array.
            StringBuilder queryBuilder = new StringBuilder("SELECT StockItemID, BinLocation FROM stockitemholdings WHERE ");
            for (int i = 0; i < productsIDs.length; i++) {
                queryBuilder.append("StockItemID = ").append(productsIDs[i]);
                if (i < productsIDs.length - 1) {
                    queryBuilder.append(" OR ");
                }
            }
            //Executing generated query
            DefaultTableModel model = Database.executeSelectQuery(queryBuilder.toString());
            for (int i = 0; i < model.getRowCount(); i++) {
                String binLocation = model.getValueAt(i, 1).toString();
                //Defining the characters within the returned model.
                char locationChar = binLocation.charAt(0);
                char locationDigit = binLocation.charAt(1);

                //Changing the value of the letter with a number to format it to the TSP algorithm
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
                //Adding the number of the location to the new array which is being returned.
                //To generate an output like this:
                // int[][] = {
                //  {1,1}
                //  {5,3}
                // }
                locationAfter[i][1] = Character.getNumericValue(locationDigit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Returning array
        return locationAfter;
    }
}
