package cs2030.simulator;

import java.util.Optional;
import cs2030.util.Pair;
import cs2030.util.ImList;

class Event {
    private final Customer customer;
    private final double time;
    private final Server server;
    private final State state;

    Event(Customer customer, double time) {
        this.customer = customer;
        this.time = time;
        this.server = new Server(0);
        this.state = State.TEST;
    }

    Event(Customer customer, double time, State state) {
        this.customer = customer;
        this.time = time;
        this.server = new Server(0);
        this.state = state;
    }

    Event(Customer customer, double time, Server server, State state) {
        this.customer = customer;
        this.time = time;
        this.server = server;
        this.state = state;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Server getServer() {
        return this.server;
    }

    public double getTime() {
        return this.time;
    }

    public State getState() {
        return this.state;
    }

    public Event swapCustomer(Customer customer) {
        return new Event(customer, this.time, this.server, this.state);
    } 

    public Event swapServer(Server server) {
        return new Event(this.customer, this.time, server, this.state);
    }

    Event createArrive() {
        return new Event(this.customer, this.time, State.ARRIVE);
    }

    Event createServe(Server server, double time) {
        return new Event(this.customer, time, server, State.SERVE);
    }

    Event createServe(Customer customer, Server server, double time) {
        return new Event(customer, time, server, State.SERVE);
    }

    Event createWait(Server server) {
        return new Event(this.customer, this.time, server, State.WAIT);
    }

    Event createDone(Server server, double time) {
        return new Event(this.customer, time, server, State.DONE);
    }

    Event createRest(Server server, double time) {
        return new Event(this.customer, time, server, State.REST);
    }

    Event createLeave() {
        return new Event(this.customer, this.time, this.server, State.LEAVE);
    }

    Event createEmpty() {
        return new Event(this.customer,this.time, State.TEST);
    }

    Statistic updateStats(Statistic stats) {
        switch (this.state) {
            case SERVE:
                double waitTime = this.time - this.customer.getArrivalTime();
                return stats.addWait(waitTime);
            case DONE: 
                return stats.addServe();
            case LEAVE:
                return stats.addLeave();
            default:
                return stats;
        }
    }

    Pair<Optional<Event>, Shop> execute(Shop shop) {
        /* 
        [0,0] -> no customer, no waiting customer
        [1,0] -> serving customer, no waiting customer
        [1,1] -> serving customer, have waiting customer
        [0,1] -> no customer, have waiting customer
        [1, 1 1] -> serving customer, have 2 waiting customer
        Arrive -> Serve -> Done -> Rest Finish -> Empty/Serve
        Arive -> Wait -> Empty
        Arrive -> (Server resting) -> Wait -> REST Finish -> Serve
        Arrive -> Leave -> Empty
        */
        ImList<Server> serverList = shop.getCanServe(this.customer);
        ImList<Server> canWaitList = shop.getCanWait();
        switch (this.state) {
            /*
            [0,0]/[1,0],/[1,1] -> ARRIVE -> [1,0]/[1,1]/[1,11] -> SERVE/WAIT/WAIT
            If no available server, ARRIVE -> LEAVE
            Update the start time of the server using serve()
            If the server is resting, wait
            WAIT and LEAVE event will not do anything
            ARRIVE will update the shop
            */
            case ARRIVE: {
                if (!serverList.isEmpty()) {
                    Server server = serverList.get(0);
                    Server newServer = server.serve(this.customer, this.time);
                    Shop nextShop = shop.updateServer(server, newServer);
                    Event newEvent = createServe(newServer, this.time);
                    return Pair.<Optional<Event>, Shop>of(Optional.<Event>of(newEvent), nextShop);
                } else if (!canWaitList.isEmpty()) {
                    Server server = canWaitList.get(0);
                    if (server.identify() == "Human Server") {
                        Server newServer = server.setWait(this.customer);
                        Shop nextShop = shop.updateServer(server, newServer);
                        Event newEvent = createWait(newServer); 
                        return Pair.<Optional<Event>, Shop>of(
                            Optional.<Event>of(newEvent), nextShop);
                    } else {
                        Shop nextShop = shop.setLine(this.customer);
                        Event newEvent = createWait(server);
                        return Pair.<Optional<Event>, Shop>of(
                            Optional.<Event>of(newEvent), nextShop);
                    }
                    
                } else {
                    return Pair.<Optional<Event>, Shop>of(Optional.<Event>of(createLeave()), shop);
                }
            }
            /*
            [1,0]/[1,1]/[1,11] -> SERVE -> [1,0]/[1,1]/[1,11] -> DONE
            Update the avaiable time of the server using updateAvailableTime
            */
            case SERVE: {
                Server newServer = this.server.updateAvailableTime(this.customer);
                // System.out.println(newServer.getAvailableTime());
                Shop nextShop = shop.updateServer(server, newServer);
                Event newEvent = createDone(this.server, this.time + this.customer.getDuration());
                return Pair.<Optional<Event>, Shop>of(
                    Optional.<Event>of(newEvent), nextShop);
            }
            /*
            [1,0]/[1,1]/[1,11] -> DONE -> [0,0]/[0,1]/[0,11] -> REST
            Update the avaiable time of server using restServer(), this allows the server to rest
            */
            case DONE: {
                Double duration = this.server.getRestDuration();
                Server server = shop.findServer(this.server.getId());
                server = server.updateRestDuration(duration);
                
                Server newServer = server.clearServer();
                newServer = newServer.restServer();
                Shop nextShop = shop.updateServer(this.server, newServer);
                Event newEvent = createRest(newServer, this.time + duration);
                return Pair.<Optional<Event>, Shop>of(
                    Optional.<Event>of(newEvent), nextShop);
            }
            /*
            [0,0] -> REST -> Empty
            [0,1]/[0,11] -> REST -> [1,0]/[1,1] -> Serve
            */ 
            case REST: {
                Server newServer = shop.findServer(this.server.getId());
                if (newServer.identify() == "Human Server") {
                    if (newServer.getQueue().isEmpty()) {
                        Shop nextShop = shop.updateServer(this.server, newServer);
                        return Pair.<Optional<Event>, Shop>of(Optional.<Event>empty(), nextShop);
                    } else {
                        newServer = newServer.serveWait(this.time);
                        Shop nextShop = shop.updateServer(this.server, newServer);
                        Event newEvent = createServe(newServer.getCustomer(), newServer, this.time);
                        return Pair.<Optional<Event>, Shop>of(
                            Optional.<Event>of(newEvent), nextShop);
                    }
                } else {
                    if (shop.getLine().isEmpty()) {
                        return Pair.<Optional<Event>, Shop>of(Optional.<Event>empty(), shop);
                    } else {
                        newServer = shop.serveLine(newServer, this.time);
                        Shop nextShop = shop.clearLine();
                        Event newEvent = createServe(newServer.getCustomer(), newServer, this.time);
                        return Pair.<Optional<Event>, Shop>of(
                            Optional.<Event>of(newEvent), nextShop);
                    }
                }
                
            }
            /*
            WAIT, LEAVE event state will not do anything
            */
            default: {

                return Pair.<Optional<Event>, Shop>of(Optional.<Event>empty(), shop);
            } 
        }

    }


    @Override
    public String toString() {
        switch (this.state) {
            case TEST: {
                return String.format("%.3f", 
                    this.time);
            }   
            case ARRIVE: {
                return String.format("%.3f %d arrives", 
                    this.getTime(), this.customer.getId());
            }
            case DONE: {
                return String.format("%.3f %d done serving by %s", 
                    this.getTime(), this.customer.getId(), this.server.toString());
            }
            case LEAVE: {
                return String.format("%.3f %d leaves", 
                    this.getTime(), this.customer.getId());
            }
            case WAIT: {
                return String.format("%.3f %d waits at %s", 
                    this.getTime(), this.customer.getId(), this.server.toString());
            }
            case SERVE: {
                return String.format("%.3f %d serves by %s", 
                    this.getTime(), this.customer.getId(), this.server.toString());
            }
            case REST: {
                return String.format("%.3f server %s finish resting ", 
                    this.getTime(), this.server.toString());
            }
            default: {
                return "";
            }
        }
        
    }
}
    
