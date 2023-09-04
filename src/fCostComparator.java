import java.util.Comparator;

public class fCostComparator implements Comparator<Station> {

    @Override
    public int compare(Station s1, Station s2) {
        if(s1.getfCost() > s2.getfCost()){
            return 1;
        } else if(s1.getfCost() < s2.getfCost()){
            return -1;
        } else {
            return 0;
        }
    }
}
