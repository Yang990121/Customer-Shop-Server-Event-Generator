package cs2030.simulator;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {
    public int compare(Event event1, Event event2) {
        double diff = event1.getTime() - event2.getTime();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } 
        double diff2 = event1.getCustomer().getId() - event2.getCustomer().getId();
        if (diff2 < 0) {
            return -1;
        } else if (diff2 > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
