import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Route {
    long route_id;
    List<Stop_time> stopTimes = new ArrayList<>();

    public Route(long route_id) {
        this.route_id = route_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return route_id == route.route_id && Objects.equals(stopTimes, route.stopTimes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route_id, stopTimes);
    }

    public void addStopTime(Stop_time s){
        stopTimes.add(s);
    }

    @Override
    public String toString() {
        return  String.valueOf(route_id);
    }
}
