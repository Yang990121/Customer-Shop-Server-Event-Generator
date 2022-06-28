package cs2030.simulator;

import java.util.List;
import cs2030.util.PQ;
import cs2030.util.ImList;
import cs2030.util.Pair;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Simulate5 {
    private final int num;
    private final ImList<Pair<Double, Supplier<Double>>> time;

    public Simulate5(int num, List<Pair<Double, Supplier<Double>>> time) {
        this.num = num;
        this.time = ImList.<Pair<Double, Supplier<Double>>>of(time);
    }

    public Shop getShop() {
        ImList<Server> list = ImList.<Server>of();
        for (int i = 1; i <= this.num; i++) {
            list = list.add(new Server(i));
        }
        return new Shop(list);
    }

    public PQ<Event> getQueue() {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> list = new PQ<Event>(cmp);
        int id = 1;
        for (Pair<Double, Supplier<Double>> i : this.time) {
            Customer customer = new Customer(id++, i.first(), 1);
            Event event = new Event(customer, i.first());
            list = list.add(event.createArrive());
        }
        return list;
    }

    public String run() {
        String output = "";
        PQ<Event> list = getQueue();
        Shop shop = getShop();
        Statistic stats = new Statistic();
        int countServe = 0;

        while (!list.isEmpty()) {
            Event temp = list.poll().first();

            if (temp.getState() == State.SERVE) {
                double duration = this.time.get(countServe).second().get();
                countServe += 1;
                Customer customer = temp.getCustomer().updateDuration(duration);
                temp = temp.swapCustomer(customer);
            }


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
