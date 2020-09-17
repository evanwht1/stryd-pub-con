package blocking;

import java.time.Duration;
import java.util.Currency;
import java.util.concurrent.TimeoutException;

/**
 * @author evanwht1@gmail.com
 */
public class BlockingBuffer<T> {

    // prevent someone else acquiring lock on this buffer and starving all threads
    private final Object lockObject = new Object();

    private final T[] entries;
    private int head = 0;
    private int size = 0;

    public BlockingBuffer(final int size) {
        // because can't instantiate a generic array
        this.entries = (T[]) new Object[size];
    }

    public void add(final T t) throws InterruptedException, TimeoutException {
        add(t, Duration.ZERO);
    }

    public void add(final T t, Duration timeout) throws InterruptedException, TimeoutException {
        synchronized (lockObject) {
            final long start = System.nanoTime();
            while (size == entries.length) {
                // wait until a get has occurred
                if (timeout.isZero()) {
                    lockObject.wait();
                } else {
                    lockObject.wait(Math.max(1, Duration.ofNanos(timeRemaining(timeout, start)).toMillis()));
                    if (timeRemaining(timeout, start) < 0) {
                        // very might have space in array but we already went over the timeout so touch luck
                        throw new TimeoutException("No space available");
                    }
                }
            }
            final int nextPos = (head + size) % entries.length;
            entries[nextPos] = t;
            size++;
            // notify one of the waiting get calls
            lockObject.notify();
        }
    }

    private long timeRemaining(Duration timeout, final long start) {
        return timeout.toNanos() - (System.nanoTime() - start);
    }

    public T get() throws InterruptedException, TimeoutException {
        return get(Duration.ZERO);
    }

    public T get(Duration timeout) throws InterruptedException, TimeoutException {
        synchronized (lockObject) {
            final long start = System.nanoTime();
            while (size == 0) {
                // wait until an add has occurred
                if (timeout.isZero()) {
                    lockObject.wait();
                } else {
                    lockObject.wait(Math.max(1, Duration.ofNanos(timeRemaining(timeout, start)).toMillis()));
                    if (timeRemaining(timeout, start) < 0) {
                        // very might have something in array but we already went over the timeout so touch luck
                        throw new TimeoutException("No objects available");
                    }
                }
            }
            final T t = entries[head];
            entries[head++] = null;
            if (head == entries.length) {
                head = 0;
            }
            size--;
            // notify one of the waiting add calls
            lockObject.notify();
            return t;
        }
    }
}
