package cs2030.simulator;

import java.util.List;
import java.util.Optional;
import cs2030.util.ImList;

class Shop {
    private final ImList<Server> servers;
    private final ImList<Customer> line;

    Shop(List<Server> list) {
        this.servers = ImList.<Server>of(list);
        this.line = ImList.<Customer>of();
    }

    Shop(ImList<Server> list) {
        this.servers = list;
        this.line = ImList.<Customer>of();
    }

    Shop(ImList<Server> list, ImList<Customer> line) {
        this.servers = list;
        this.line = line;
    }

    public ImList<Server> getServer() {
        return this.servers;
    }

    public ImList<Customer> getLine() {
        return this.line;
    }

    boolean haveLine(Server server) {
        if (this.line.size() < server.getLimit()) {
            return false;
        } else {
            return true;
        }
    }

    Shop setLine(Customer customer) {
        ImList<Customer> list = ImList.<Customer>of();
        list = list.addAll(this.line);
        list = list.add(customer);
        return new Shop(this.servers, list);
        
    }

    Customer getInLine() {
        return this.line.get(0);
    }

    SelfCheckOut serveLine(Server server, double startTime) {
        Customer customer = getInLine();
        ImList<Customer> temp = ImList.<Customer>of();
        temp = temp.add(customer);
        return new SelfCheckOut(server.getId(), server.getLimit(),temp);
    }

    Shop clearLine() {
        ImList<Customer> temp = ImList.<Customer>of(line);
        temp = temp.remove(0).second();
        return new Shop(this.servers, temp);
    }

    public Server findServer(int id) {
        for (Server s : this.servers) {
            if (s.getId() == id) {
                return s;
            }
        }
        return new Server(-1, 0);
    }

    public Shop updateServer(Server s1, Server s2) {
        ImList<Server> list = ImList.<Server>of();
        for (Server i : this.servers) {
            if (i.getId() == s1.getId()) {
                list = list.add(s2);
            } else {
                list = list.add(i);
            }
        }
        return new Shop(list, this.line);
    }

    public ImList<Server> getCanServe(Customer customer) {
        ImList<Server> list = ImList.<Server>of();
        for (Server i : this.servers) {
            if (i.canServe(customer)) {
                list = list.add(i);
            }
        }
        return list;
    }

    public ImList<Server> getCanWait() {
        ImList<Server> list = ImList.<Server>of();
        for (Server i : this.servers) {
            if (i.identify() == "Human Server") {
                if (i.haveWait() == false) {
                    list = list.add(i);
                }    
            } else {
                if (haveLine(i) == false) {
                    list = list.add(i);
                }
            }
            
        }
        return list;
    }

    @Override 
    public String toString() {
        return this.servers.toString();
    }
}

