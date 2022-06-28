package cs2030.simulator;

import java.util.List;
import cs2030.util.PQ;
import cs2030.util.ImList;
import cs2030.util.Pair;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class Simulate4 {
    private final int num;
    private final ImList<Double> time;

    public Simulate4(int num, List<Double> time) {
        this.num = num;
        this.time = ImList.<Double>of(time);
    }

    public PQ<Event> getQueue() {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> list = new PQ<Event>(cmp);
        int id = 1;
        for (Double i : this.time) {
            Customer customer = new Customer(id++, i);
            Event event = new Event(customer, i);
            list = list.add(event.createArrive());
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
        Shop shop = getShop();
        Statistic stats = new Statistic();

        while (!list.isEmpty()) {
            Event temp = list.poll().first();

            list = list.poll().second();
            Pair<Optional<Event>, Shop> next = temp.execute(shop);
            Optional<Event> event = next.first();

            Event nextEvent = event.orElse(temp.createEmpty());
            if (nextEvent.getState() != State.TEST) {
                list = list.add(nextEvent);
                stats = nextEvent.updateStats(stats);
            }

            Shop nextShop = next.second();
            shop = nextShop;
            if (temp.getState() != State.REST) {
                output += temp.toString() + "\n";
            }
        }
        output += stats.toString();
        return output;
    }

    @Override
    public String toString() {
        String output = String.format("Queue: %s; Shop: %s", 
                getQueue().toString(), getShop().toString());
        return output;
    }
}
