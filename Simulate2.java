package cs2030.simulator;

import java.util.List;
import cs2030.util.PQ;
import cs2030.util.ImList;
import java.util.Comparator;


public class Simulate2 {
    private final int num;
    private final ImList<Double> time;

    public Simulate2(int num, List<Double> time) {
        this.num = num;
        this.time = ImList.<Double>of(time);
    }

    public PQ<Event> getQueue() {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> list = new PQ<Event>(cmp);
        int id = 1;
        for (Double i : this.time) {
            Customer customer = new Customer(id++, i);
            Event event = new EventStub(customer, i);
            list = list.add(event);
        }
        return list;
    }

    public Shop getShop() {
        ImList<Server> list = ImList.<Server>of();
        for (int i = 1; i <= this.num; i++) {
            list = list.add(new Server(i));
        }
        return new Shop(list);
    }

    public String run() {
        String output = "";
        PQ<Event> list = getQueue();
        while (!list.isEmpty()) {
            Event temp = list.poll().first();
            list = list.poll().second();
            output += temp.toString() + "\n";
        }
        output += "-- End of Simulation --";
        return output;
    }

    @Override
    public String toString() {
        String output = String.format("Queue: %s; Shop: %s", 
                getQueue().toString(), getShop().toString());
        return output;
    }
}
