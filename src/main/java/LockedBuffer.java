import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author evanwht1@gmail.com
 */
public class LockedBuffer<T> implements MessageQueue<T> {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition itemAvailable = lock.newCondition();
    private final Condition spaceAvailable = lock.newCondition();

    private final T[] entries;
    private int head = 0;
    private int size = 0;

    public LockedBuffer(final int size) {
        // because can't instantiate a generic array
        this.entries = (T[]) new Object[size];
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            final long start = System.nanoTime();
            while (size == entries.length) {
                if (timeout.isZero()) {
                    // 0 = on timeout
                    spaceAvailable.await();
                } else {
                    // wait for a minimum of 1 us. putting 0 would make this wait indefinitely
                    if (!spaceAvailable.await(Math.max(1, timeRemaining(timeout, start)), TimeUnit.NANOSECONDS)) {
                        throw new TimeoutException("No space available");
                    }
                }
            }
            final int nextPos = (head + size) % entries.length;
            entries[nextPos] = t;
            size++;
            itemAvailable.signal();
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            final long start = System.nanoTime();
            while (size == 0) {
                if (timeout.isZero()) {
                    // 0 = no timeout
                    itemAvailable.await();
                } else {
                    // wait for a minimum of 1 us. putting 0 would make this wait indefinitely
                    if (!itemAvailable.await(Math.max(1, timeRemaining(timeout, start)), TimeUnit.NANOSECONDS)) {
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
            spaceAvailable.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }
}
