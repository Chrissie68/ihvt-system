import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class TSPAlgorithm{

    //Calculate the distance between 2 product locations.
    private static double distance(Productlocatie p1, Productlocatie p2) {
        //Hij loopt stuk als hij een oneven getal moet uitrekenen
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    //Calculate the distance of the route by using the distance function.
    public static double routeDistance(Productlocatie[] route) {
        double totalDistance = 0;
        //Looping through the entire route
        for (int i = 0; i < route.length - 1; i++) {
            //Using i+1 to compare the current coordinate against the one next in line.
            totalDistance += distance(route[i], route[i + 1]);
        }
        //Returning to the 0.0 point.
        totalDistance += distance(route[route.length - 1], route[0]);
        return totalDistance;
    }

    //Function to generate possible routes. It makes an arraylist, which is being used by the helper function below.
    private static List<Productlocatie[]> generatePossibleRoutes(Productlocatie[] points) {
        List<Productlocatie[]> possibleRoutes = new ArrayList<>();

        possibleRouteHelp(points, 0, possibleRoutes);
        return possibleRoutes;
    }

    //Function to help the possibleRoutes, it calculates the distance for each possible route and adds these to the arraylist from generatePossibleRoutes
    private static void possibleRouteHelp(Productlocatie[] points, int index, List<Productlocatie[]> possibleRoutes) {
        if (index == points.length) {
            //If the function has added all the points, it will add the route to the variable.
            possibleRoutes.add(points.clone());
        } else {
            for (int i = index; i < points.length; i++) {
                //Swapping the locations of the indexes around to make sure all locations are being compared with eachother
                swap(points, index, i);
                //Added 1 to the index so the function keeps recalling itself untill all possible outcomes have been added.
                possibleRouteHelp(points, index + 1, possibleRoutes);
                swap(points, index, i);
            }
        }
    }

    //Function to swap 2 locations to compare them the other way around
    private static void swap(Productlocatie[] points, int i, int j) {
        Productlocatie swapTemporary = points[i];
        points[i] = points[j];
        points[j] = swapTemporary;
    }


    //Function to calculate the fastest route
    public static Productlocatie[] findFastestRoute(Productlocatie[] points) {
        List<Productlocatie[]> possibleRoutes = generatePossibleRoutes(points);
        Productlocatie[] fastestRoute = null;
        double minDistance = Double.MAX_VALUE;
        for (Productlocatie[] route : possibleRoutes) {
            //For each route in possibleRoutes take the distance
            double distance = routeDistance(route);
            //If the distance is smaller than the minDistance, make the minimal distance, the distance
            if (distance < minDistance) {
                minDistance = distance;
                fastestRoute = route;
            }
        }
        return fastestRoute;
    }

    public static void addLocationsGetResults(int[][] locatie){
        //Array with letters from A-Z to append a name to a location.
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        //New array to make more productlocations with added letters.
        Productlocatie[] locaties = new Productlocatie[locatie.length];
        for(int i = 0; i < locatie.length; i++){
            locaties[i] = new Productlocatie(letters[i], locatie[i][0], locatie[i][1]);
        }
        Productlocatie[] fastestRoute = TSPAlgorithm.findFastestRoute(locaties);
        System.out.println("De route:");
        Arrays.stream(fastestRoute).forEach(p -> System.out.println("(" + p.name + ": " + p.x + ", " + p.y + ")"));S

        //FUTURE: Make function that sends the locations to the Arduino.
    }
}