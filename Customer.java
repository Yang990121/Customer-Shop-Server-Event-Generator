package cs2030.simulator;

import java.util.function.Supplier;
import cs2030.util.Pair;

class Customer {
    private final int id;
    private final Pair<Double, Supplier<Double>> time;

    /*
    Customer() {
        this.id = 0;
        this.time = Pair.<Double, Supplier<Double>>of(-1.0, () -> 1.0);
    }
    */

    Customer(int id, double arrivalTime) {
        this.id = id;
        this.time = Pair.<Double, Supplier<Double>>of(arrivalTime, () -> 1.0);
    }

    Customer(int id, double arrivalTime, double duration) {
        this.id = id;
        this.time = Pair.<Double, Supplier<Double>>of(arrivalTime, () -> duration);
    }

    /*
    Customer(int id, Pair<Double, Supplier<Double>> time) {
        this.id = id;
        this.time = time;
    }
    */


    public int getId() {
        return this.id;
    }

    public Pair<Double, Supplier<Double>> getTime() {
        return this.time;
    }

    public double getArrivalTime() {
        return this.time.first();
    }

    public double getDuration() {
        return this.time.second().get();
    }
    

    public Customer updateDuration(double time) {
        return new Customer(this.id, getArrivalTime(), time);
    }

    @Override
    public String toString() {
        return "" + this.id;
    }
}
