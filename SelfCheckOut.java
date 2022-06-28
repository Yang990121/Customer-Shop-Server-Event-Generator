package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.PQ;

class SelfCheckOut extends Server {
    //private static final ImList<Customer> queue = ImList.<Customer>of()
    
    SelfCheckOut(int id) {
        super(id, 1, 0.0, 0.0, ImList.<Customer>of(), ImList.<Customer>of());
    }

    SelfCheckOut(int id, int limit, ImList<Customer> customer) {
        super(id, limit, 0.0, 0.0, customer, ImList.<Customer>of());
    }

    SelfCheckOut(int id, int limit) {
        super(id, limit, 0.0, 0.0, ImList.<Customer>of(), ImList.<Customer>of());
    }

    SelfCheckOut(int id, int limit, double availableTime, 
        double restDuration, ImList<Customer> customer, ImList<Customer> waitingCustomer) {
        super(id, limit, availableTime, 0.0, customer, waitingCustomer);
    }

    @Override
    public String identify() {
        return "Self CheckOut";
    }

    
    @Override
    double getRestDuration() {
        return 0;
    }

    @Override
    Server updateRestDuration(double time) {
        return this;
    }

    @Override
    SelfCheckOut restServer() {
        return this;
    }

    @Override
    Server serve(Customer customer, Double startTime) {
        ImList<Customer> temp = ImList.<Customer>of();
        temp = temp.add(customer);
        return new SelfCheckOut(
            super.getId(), 
            super.getLimit(), 
            startTime, 
            0, 
            temp, 
            ImList.<Customer>of());
    }

    @Override
    Server updateAvailableTime(Customer customer) {
        ImList<Customer> temp = ImList.<Customer>of();
        temp = temp.add(customer);
        Double nextTime = super.getAvailableTime() + customer.getDuration();
        return new SelfCheckOut(
            super.getId(), 
            super.getLimit(), 
            nextTime, 
            0, 
            temp, 
            ImList.<Customer>of());
    }

    @Override
    Server clearServer() {
        ImList<Customer> list = ImList.<Customer>of();
        return new SelfCheckOut(
            super.getId(), 
            super.getLimit(), 
            super.getAvailableTime(), 
            0, 
            list, ImList.<Customer>of());
    }

    @Override
    public String toString() {
        return "self-check " + super.getId();
    }

}

