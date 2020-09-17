package blocking;

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

    public boolean add(final T t) {
        synchronized (lockObject) {
            if (size == entries.length) {
                return false;
            } else {
                final int nextPos = (head + size) % entries.length;
                entries[nextPos] = t;
                size++;
                return true;
            }
        }
    }

    public synchronized T get() {
        synchronized (lockObject) {
            if (size == 0) {
                return null;
            } else {
                final T t = entries[head];
                entries[head++] = null;
                if (head == entries.length) {
                    head = 0;
                }
                size--;
                return t;
            }
        }
    }
}
