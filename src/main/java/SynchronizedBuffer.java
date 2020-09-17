import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * @author evanwht1@gmail.com
 */
public class SynchronizedBuffer<T> implements MessageQueue<T> {

    // prevent someone else acquiring lock on this buffer and starving all threads
    private final Object lock = new Object();

    private final T[] entries;
    private int head = 0;
    private int size = 0;

    public SynchronizedBuffer(final int size) {
        // because can't instantiate a generic array
        this.entries = (T[]) new Object[size];
    }

    @Override
    public int size() {
        synchronized (lock) {
            return size;
        }
    }

    @Override
    public void add(final T t) throws InterruptedException {
        try {
            add(t, Duration.ZERO);
        } catch (TimeoutException e) {
            // no op. Duration.ZERO means no timeout
        }
    }

    @Override
    public void add(final T t, Duration timeout) throws InterruptedException, TimeoutException {
        synchronized (lock) {
            final long start = System.nanoTime();
            while (size == entries.length) {
                // wait until a get has occurred
                if (timeout.isZero()) {
                    // 0 = on timeout
                    lock.wait();
                } else {
                    // wait for a minimum of 1 ms. putting 0 would make this wait indefinitely
                    lock.wait(Math.max(1, Duration.ofNanos(timeRemaining(timeout, start)).toMillis()));
                    if (timeRemaining(timeout, start) < 0) {
                        // might have space in array but we already went over the timeout so tough luck
                        throw new TimeoutException("No space available");
                    }
                }
            }
            final int nextPos = (head + size) % entries.length;
            entries[nextPos] = t;
            size++;
            // notify one of the waiting get calls
            lock.notify();
        }
    }

    private long timeRemaining(Duration timeout, final long start) {
        return timeout.toNanos() - (System.nanoTime() - start);
    }

    @Override
    public T get() throws InterruptedException {
        try {
            return get(Duration.ZERO);
        } catch (TimeoutException e) {
            // no op. Duration.ZERO means no timeout
            return null;
        }
    }

    @Override
    public T get(Duration timeout) throws InterruptedException, TimeoutException {
        synchronized (lock) {
            final long start = System.nanoTime();
            while (size == 0) {
                // wait until an add has occurred
                if (timeout.isZero()) {
                    // 0 = no timeout
                    lock.wait();
                } else {
                    // wait for a minimum of 1 ms. putting 0 would make this wait indefinitely
                    lock.wait(Math.max(1, Duration.ofNanos(timeRemaining(timeout, start)).toMillis()));
                    if (timeRemaining(timeout, start) < 0) {
                        // might have something in array but we already went over the timeout so tough luck
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
            lock.notify();
            return t;
        }
    }
}
