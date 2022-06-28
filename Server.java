package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.PQ;

class Server {
    /*
    Take note customer and waiting customer are in list
    When getting customer, need to make sure the list is not empty
    */
    private final int id;
    private final int limit;
    private final double availableTime;
    private final double restDuration;
    private final ImList<Customer> customer;
    private final ImList<Customer> waitingCustomer;

    Server(int id) {
        this.id = id;
        this.limit = 1;
        this.availableTime = 0.0;
        this.restDuration = 0.0;
        this.customer = ImList.<Customer>of();
        this.waitingCustomer = ImList.<Customer>of();
    }

    Server(int id, int limit) {
        this.id = id;
        this.limit = limit;
        this.availableTime = 0.0;
        this.restDuration = 0.0;
        this.customer = ImList.<Customer>of();
        this.waitingCustomer = ImList.<Customer>of();
    }

    Server(int id, int limit, double availableTime, double restDuration, 
        ImList<Customer> customer, ImList<Customer> waitingCustomer) {
        this.id = id;
        this.limit = limit;
        this.availableTime = availableTime;
        this.restDuration = restDuration;
        this.customer = customer;
        this.waitingCustomer = waitingCustomer;
    }

    public String identify() {
        return "Human Server";
    }

    int getId() {
        return this.id;
    }

    int getLimit() {
        return this.limit;
    }

    double getAvailableTime() {
        return this.availableTime;
    }

    double getRestDuration() {
        return this.restDuration;
    }

    ImList<Customer> getCustomerList() {
        return this.customer;
    }

    ImList<Customer> getQueue() {
        return this.waitingCustomer;
    }

    Server updateRestDuration(double time) {
        return new Server(
            this.id, 
            this.limit, 
            this.availableTime, 
            time, 
            this.customer, 
            this.waitingCustomer);
    }

    Customer getCustomer() {
        return this.customer.get(0);
    }

    Customer getWaitingCustomer() {
        return this.waitingCustomer.get(0);
    }

    boolean canServe(Customer customer) {
        if (this.customer.isEmpty() && this.availableTime <= customer.getArrivalTime()) {
            return true;
        } 
        return false;
    }

    boolean haveWait() {
        if (this.waitingCustomer.size() < this.limit) {
            return false;
        } else {
            return true;
        }
    }

    Server setWait(Customer customer) {
        ImList<Customer> list = this.waitingCustomer;
        list = list.add(customer);
        return new Server(
            this.id, 
            this.limit, 
            this.availableTime, 
            this.restDuration, 
            this.customer, 
            list);
    }

    Server serve(Customer customer, Double startTime) {
        ImList<Customer> temp = ImList.<Customer>of();
        temp = temp.add(customer);
        return new Server(
            this.id, 
            this.limit, 
            startTime, 
            this.restDuration, 
            temp, 
            this.waitingCustomer);
    }

    Server updateAvailableTime(Customer customer) {
        ImList<Customer> temp = ImList.<Customer>of();
        temp = temp.add(customer);
        Double nextTime = this.availableTime + customer.getDuration();
        return new Server(
            this.id, 
            this.limit, 
            nextTime, 
            this.restDuration, 
            temp, 
            this.waitingCustomer);
    }

    Server clearServer() {
        ImList<Customer> list = ImList.<Customer>of();
        return new Server(
            this.id, 
            this.limit, 
            this.availableTime, 
            this.restDuration, 
            list, 
            this.waitingCustomer);
    }

    Server serveWait(double startTime) {
        Customer customer = getWaitingCustomer();
        ImList<Customer> temp = ImList.<Customer>of();
        temp = temp.add(customer);
        return new Server(this.id, 
            this.limit, 
            startTime, 
            this.restDuration, 
            temp, 
            this.waitingCustomer.remove(0).second());
    }

    Server restServer() {
        return new Server(
            this.id, 
            this.limit, 
            this.availableTime + this.restDuration, 
            this.restDuration, 
            this.customer, 
            this.waitingCustomer);
    }


    @Override
    public String toString() {
        return "" + this.id;
    }
}
