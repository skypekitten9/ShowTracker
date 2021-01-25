package showtracker.server;

import java.util.LinkedList;

public class Buffer<T> {
    private LinkedList<T> buffer = new LinkedList<T>();

    public synchronized void put(T obj) {
        buffer.addLast(obj);
        notifyAll();
    }

    public synchronized T get() {
        while(buffer.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Buffer: " + e);
            }
        }
        return buffer.removeFirst();
    }
}