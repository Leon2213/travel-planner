import java.io.*;
import java.util.*;


public class Main {
    static List<Station> stationsList = new ArrayList<>();
    static Map<Long, Route> IDtoRouteMap = new HashMap<>();
    static Map<Integer, Station> IDtoStationMap = new HashMap<>();
    static Map<String, Station> NameToStationMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        readStops();
        readStopTimes();
        startPrompt();
    }

    private static void startPrompt() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n");
        System.out.println("-Welcome to the route planner-");
        String input = "y";
        while(input.equalsIgnoreCase("y")){
            Station from = null;
            Station to = null;
            String time = "";
            while (from == null) {
                System.out.println(" ");
                System.out.println("Starting station?: ");
                String fr = sc.nextLine();
                from = NameToStationMap.get(fr);
                if (from == null) {
                    System.out.println("Try again");
                }
            }
            while (to == null) {
                System.out.println("End destation?: ");
                String t = sc.nextLine();
                to = NameToStationMap.get(t);
                if (to == null) {
                    System.out.println("Try again");
                }
            }
            while (time.isEmpty()) {
                System.out.println("Time XX:XX?:");
                time = sc.nextLine();
                if (time.isEmpty()) {
                    System.out.println("Try again");
                }
            }
            fastestRoute(from, to, time);
            System.out.println("Search a new time? y/n");
            input = sc.nextLine();
        }

    }

    private static void readStopTimes() throws IOException {
        //File file2 = new File("C:\\Users\\Test\\Desktop\\ALDA\\reseplanerare\\sl_stop_times.txt");
        File file2 = new File("C:\\Users\\Test\\Desktop\\reseplanerareAlda\\datafiles\\sl_stop_times.txt");
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        long old_trip_id = -1;
        Station last_station = null;
        String line2 = "";
        br2.readLine();
        while ((line2 = br2.readLine()) != null) {
            String[] split = line2.split(",");
            long trip_id = Long.valueOf(split[0]);
            String aTimeString = split[1];
            String dTimeString = split[2];
            aTimeString = fixFormat(aTimeString);
            dTimeString = fixFormat(dTimeString);
            int stop_id = Integer.valueOf(split[3]);
            int stop_sequence = Integer.valueOf(split[4]);
            int pickup_type = Integer.valueOf(split[5]);
            int drop_off_type = Integer.valueOf(split[6]);
            Stop_time stop_time = new Stop_time(trip_id, aTimeString, dTimeString, stop_id, stop_sequence, pickup_type, drop_off_type);
            boolean routeExists = checkIfRouteExists(trip_id, IDtoRouteMap);
            Station currentStation = IDtoStationMap.get(stop_id);
            if (routeExists) {
                Route route = IDtoRouteMap.get(trip_id);
                if (route != null) {
                    route.addStopTime(stop_time);
                    if (!currentStation.getRoutes().contains(route)) {
                        currentStation.addRoute(route);
                    }
                }
            } else {
                Route route = new Route(trip_id);
                IDtoRouteMap.put(trip_id, route);
                route.addStopTime(stop_time);
                if (!currentStation.getRoutes().contains(route)) {
                    currentStation.addRoute(route);
                }
            }
            if (trip_id == old_trip_id) {
                if (last_station != null && !last_station.getNeighbors().contains(currentStation)) {
                    last_station.addNeighbor(currentStation);
                }
            }
            if (last_station != null) {
                Map<Station, List<Stop_time>> arriveAtStationMap = last_station.getArriveAtStation();
                if (!arriveAtStationMap.keySet().isEmpty()) {
                    List<Stop_time> stop_times = arriveAtStationMap.get(currentStation);
                    if (stop_times != null) {
                        stop_times.add(stop_time);
                    } else {
                        List<Stop_time> stop_timesNew = new ArrayList<>();
                        stop_timesNew.add(stop_time);
                        arriveAtStationMap.put(currentStation, stop_timesNew);
                        last_station.setArriveAtStation(arriveAtStationMap);
                    }
                } else {
                    List<Stop_time> stop_timesList = new ArrayList<>();
                    stop_timesList.add(stop_time);
                    Map<Station, List<Stop_time>> newMap = new HashMap<>();
                    newMap.put(currentStation, stop_timesList);
                    last_station.setArriveAtStation(newMap);
                }
            }
            last_station = currentStation;
            old_trip_id = trip_id;
        }
    }


    private static void readStops() throws IOException {
        File file1 = new File("C:\\Users\\Test\\Desktop\\ALDA\\reseplanerare\\sl_stops.txt");
        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        String line1;
        br1.readLine();
        while ((line1 = br1.readLine()) != null) {
            String[] split = line1.split(",");
            int stop_id = Integer.valueOf(split[0]);
            String stop_name = split[1];
            double stop_lat = Double.valueOf(split[2]);
            double stop_long = Double.valueOf(split[3]);
            Station station = new Station(stop_id, stop_name, stop_lat, stop_long);
            stationsList.add(station);
            IDtoStationMap.put(stop_id, station);
            NameToStationMap.put(stop_name, station);
        }
        br1.close();
    }

    /**
     * The method, with the help of sub-methods, prints the fastest route between two Stations.
     * It takes  two stations as input and a starting time, and finds the fastest route
     * between those two stations for a given time.
     *
     * @param fromStation the start station from which the user wants to travel from.
     * @param toStation   the end station from which to the user wants to travel too.
     * @param sTime       the time user requests to start the trip.
     * @return            the method does not return any value.
     */

    private static void fastestRoute(Station fromStation, Station toStation, String sTime) {
        String startTime = sTime;
        fromStation.setArrivalTimeString(startTime);
        PriorityQueue<Station> open = new PriorityQueue<>(new fCostComparator());
        HashSet<Station> closed = new HashSet<>();
        open.add(fromStation);
        while (open.size() > 0) {
            Station currentStation = open.poll();
            closed.add(currentStation);
            if (currentStation == toStation) {
                retracePath(fromStation, toStation, sTime);
            }
            for (Station neighbor : currentStation.getNeighbors()) {
                startTime = currentStation.getArrivalTimeString();
                if (neighbor.getNeighbors().isEmpty() || closed.contains(neighbor)) {
                    continue;
                }
                Stop_time bestTime = currentStation.getBestArrivalTimeToNeighbor(neighbor, startTime);
                if (bestTime != null) {
                    String arrivalTime = bestTime.getaTime();
                    double diffCost = timeDiff(arrivalTime, startTime);
                    double newPathToNeighbor = currentStation.getgCost() + diffCost;
                    if (newPathToNeighbor < neighbor.getgCost() || !open.contains(neighbor)) {
                        updateNeighborCost(newPathToNeighbor, neighbor, currentStation, bestTime, open,  toStation);
                    }
                }
            }
        }
        return;
    }

    /** The method is a help-method to the parent-method fastestRoute.
     * This method is called in the parent-method when a new faster route
     * is found from the starting station, to the station that's currently being explored.
     *
     * Once this method is called, a new faster route has been found to this station
     * and the stations is updated with a new F-cost, G-cost, it's parent (where this station
     * was visited from) is updated, and it's arrivalTime from it's parent is updated.
     *
     * @param newPathToNeighbor The new shorter path, in minutes, that has been found for this station.
     * @param currentStation    the current station which information will be updated in this method.
     * @param parentStation     the station from where we came from before this station.
     * @param bestTime          an object representing the arrival time from the parentnode.
     * @param open              the priority-queue which holds the station ordered by lowest F-cost.
     * @param toStation         the destination station from the parent-method.
     */
    private static void updateNeighborCost(double newPathToNeighbor, Station currentStation, Station parentStation, Stop_time bestTime, PriorityQueue<Station> open, Station toStation) {
        Station stationA = NameToStationMap.get("Hallonbergen T-bana");
        Station stationB = NameToStationMap.get("Kista T-bana");
        double distance = stationA.getPos().distance(stationB.getPos());
        double minuteDistance = distance / 5;
        currentStation.setgCost(newPathToNeighbor);
        double distanceLength = currentStation.getPos().distance(toStation.getPos());
        double hCostTime = distanceLength / minuteDistance;
        currentStation.sethCost(hCostTime); // här är geo-jämförelsen, hur långt koordinatmässigt det är kvar
        currentStation.setParent(parentStation);
        currentStation.setArrivalTime(bestTime);
        currentStation.setArrivalTimeString(bestTime.getaTime());
        currentStation.setFcost(currentStation.getgCost(), currentStation.gethCost());
        if (!open.contains(currentStation)) {
            open.add(currentStation);
        }
    }

    public static double timeDiff(String time1, String time2) {
        String[] time1StringArr = time1.split(":");
        int time1Hour = Integer.valueOf(time1StringArr[0]);
        int time1Min = Integer.valueOf(time1StringArr[1]);
        String[] time2StringArr = time2.split(":");
        int time2Hour = Integer.valueOf(time2StringArr[0]);
        int time2Min = Integer.valueOf(time2StringArr[1]);
        double hDiff = time1Hour - time2Hour;
        double mDiff = time1Min - time2Min;
        mDiff = mDiff / 60;
        double totalDiff = hDiff + mDiff;
        return totalDiff * 60;

    }

    public static void retracePath(Station startStation, Station endStation, String sTime) {
        ArrayList<Station> path = new ArrayList<>();
        Station currentStation = endStation;
        while (currentStation != startStation) {
            path.add(currentStation);
            currentStation = currentStation.getParent();
        }
        Collections.reverse(path);
        System.out.println("\n\n\n\n");
        System.out.println("For start destination: " + startStation.getStop_name());
        System.out.println("To end destination: " + endStation.getStop_name());
        System.out.println("For requested time : " + sTime);
        System.out.println("Arrival time to end desination: " + endStation.getArrivalTimeString());
        System.out.println(" ");
        System.out.println("The route to take is: ");
        System.out.println(" ");
        System.out.println("From " + startStation.getStop_name() + ": ");
        System.out.println("--------------------");
        for (Station s : path) {
            System.out.println(" Arrivaltime: " + s.getArrivalTimeString());
            System.out.println(" " + s.getStop_name());
            System.out.println("--------------------");
        }
        System.out.println(" ");
    }


    private static String fixFormat(String string) {
        LinkedList<Character> arrivalTime = new LinkedList<>();
        char[] charArr = new char[5];
        for (int i = 0; i < string.length() - 3; i++) {
            arrivalTime.add(i, string.charAt(i));
        }
        if (string.charAt(1) == ':') {
            arrivalTime.add(0, '0');
        }
        int n = 0;
        for (Character c : arrivalTime) {
            charArr[n + 0] = c;
            n++;
        }
        string = String.valueOf(charArr);
        return string;
    }

    private static boolean checkIfRouteExists(Long trip_id, Map<Long, Route> routeMap) {
        if (routeMap == null) {
            return false;
        }
        if (routeMap.get(trip_id) != null) {
            return true;
        }
        return false;
    }

}
