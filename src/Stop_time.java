public class Stop_time {
    private long trip_id;
    private String aTime;
    private String dTime;
    private int stop_id;
    private int stop_sequence;
    private int pickup_type;
    private int drop_off_type;

    public Stop_time(long trip_id, String aTime, String dTime, int stop_id, int stop_sequence, int pickup_type, int drop_off_type) {
        this.trip_id = trip_id;
        this.aTime = aTime;
        this.dTime = dTime;
        this.stop_id = stop_id;
        this.stop_sequence = stop_sequence;
        this.pickup_type = pickup_type;
        this.drop_off_type = drop_off_type;
    }

    public long getTrip_id() {
        return trip_id;
    }


    public String getaTime() {
        return aTime;
    }

    public int getHour(){
        String[] split = aTime.split(":");
        String hourStr = split[0];
        return Integer.valueOf(hourStr);

    }

    public int getMinutes(){
        String[] split = aTime.split(":");
        String minutesStr = split[1];
        return Integer.valueOf(minutesStr);
    }

    @Override
    public String toString() {
        return "Stop_time{" +
                "trip_id=" + trip_id +
                ", aTime='" + aTime + '\'' +
                ", dTime='" + dTime + '\'' +
                ", stop_id=" + stop_id +
                ", stop_sequence=" + stop_sequence +
                ", pickup_type=" + pickup_type +
                ", drop_off_type=" + drop_off_type +
                '}';
    }
}
