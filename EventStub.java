package cs2030.simulator;

import java.util.Optional;
import cs2030.util.Pair;

class EventStub extends Event {

    EventStub(Customer customer, double time) {
        super(customer, time, State.TEST);
    }
    
    public Pair<Optional<Event>, Shop> execute(Shop shop) {        
        return Pair.<Optional<Event>, Shop>of(Optional.<Event>empty(), shop);
    }

}
