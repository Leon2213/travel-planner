import java.awt.geom.Point2D;
import java.util.*;

public class Station {
    private int stop_id;
    private String stop_name;
    private Point2D.Double coordinatePos;
    private double gCost;
    private double hCost;
    private double fCost;
    private Station parent;
    private Stop_time arrivalTime = null;
    private String arrivalTimeString = "";
    private ArrayList<Stop_time> bestRouteList = new ArrayList<>();
    List<Station> neighbors = new ArrayList<>();
    List<Route> routes = new ArrayList<>();
    Map<Station, List<Stop_time>> arriveAtStation = new HashMap<>();


    public Station(int stop_id, String stop_name, double stop_lat, double stop_long) {
        this.stop_id = stop_id;
        this.stop_name = stop_name;
        this.coordinatePos = new Point2D.Double(stop_lat, stop_long);
    }

    public void setFcost(double gCost, double hCost){
        fCost = gCost + hCost;
    }
    public double getfCost(){
        return fCost;
    }

    public Station getParent() {
        return parent;
    }

    public void setParent(Station parent) {
        this.parent = parent;
    }

    public Point2D.Double getPos() {
        return coordinatePos;
    }

    public void setArrivalTime(Stop_time time){
        this.arrivalTime = time;
    }

    public void setArrivalTimeString(String time) {
        this.arrivalTimeString = time;
    }

    public String getArrivalTimeString() {
        return arrivalTimeString;
    }

    public Stop_time getArrivalTime() {
        return arrivalTime;
    }

    public double getgCost() {
        return gCost;
    }

    public void setgCost(double gCost) {
        this.gCost = gCost;
    }

    public double gethCost() {
        return hCost;
    }

    public void sethCost(double hCost) {
        this.hCost = hCost;
    }


    public String getStop_name() {
        return stop_name;
    }


    public List<Route> getRoutes() {
        return routes;
    }


    public void addRoute(Route route) {
        routes.add(route);
    }

    public List<Station> getNeighbors() {
        return neighbors;
    }


    public void addNeighbor(Station station) {
        neighbors.add(station);
    }

    public Map<Station, List<Stop_time>> getArriveAtStation() {
        return arriveAtStation;
    }

    public List<Stop_time> getNeighborArrivalTimes(Station station) {
        return arriveAtStation.get(station);
    }

    public void setArriveAtStation(Map<Station, List<Stop_time>> arriveAtStation) {
        this.arriveAtStation = arriveAtStation;
    }


    public Stop_time getBestArrivalTimeToNeighbor(Station toStation, String startTime) {
        double lowestValue = 9999999.0;
        Stop_time bestStopTime = null;
        List<Stop_time> toStationTimeTable = this.getNeighborArrivalTimes(toStation);
        String[] startTimeStringArr = startTime.split(":");
        int startTimeHour = Integer.valueOf(startTimeStringArr[0]);
        int startTimeMinute = Integer.valueOf(startTimeStringArr[1]);
        for (Stop_time stop_time : toStationTimeTable) {
            int arrTimeHour = stop_time.getHour();
            Double hDiff = (double) arrTimeHour - (double) startTimeHour;
            int arrTimeMinute = stop_time.getMinutes();
            Double mDiff = (double) arrTimeMinute - (double) startTimeMinute;
            mDiff = mDiff / 60;
            double totalDiff = hDiff + mDiff;
            if (totalDiff < lowestValue && totalDiff > 0) {
                lowestValue = totalDiff;
                bestStopTime = stop_time;
            }
        }
        return bestStopTime;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "stop_id=" + stop_id +
                ", stop_name='" + stop_name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return stop_id == station.stop_id && Objects.equals(stop_name, station.stop_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stop_id, stop_name);
    }
}

