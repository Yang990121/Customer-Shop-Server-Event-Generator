package cs2030.util;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;

public class PQ<T> {
    private final Queue<T> queue;

    public PQ(Comparator<? super T> cmp) {
        this.queue = new PriorityQueue<T>(cmp);
    } 

    public PQ(Queue<? extends T> list) {
        this.queue = new PriorityQueue<T>(list); 
    }

    public PQ<T> add(T elem) {
        PQ<T> temp = new PQ<T>(this.queue);
        temp.queue.add(elem);
        return temp;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public Pair<T, PQ<T>> poll() {
        PQ<T> temp = new PQ<T>(this.queue);
        T removed = temp.queue.poll();
        return Pair.<T, PQ<T>>of(removed, temp);
    }

    @Override 
    public String toString() {
        return this.queue.toString();
    }


}
