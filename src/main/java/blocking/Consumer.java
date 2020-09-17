package blocking;

import java.time.Duration;

/**
 * Consumer of objects from a queue that will block the thread that called until an object is available or
 * it times out.
 *
 * @author evanwht1@gmail.com
 */
public class Consumer<T> {

    private final BlockingBuffer<T> queue;

    public Consumer(final BlockingBuffer<T> queue) {
        this.queue = queue;
    }

    /**
     * Publishes an object to a message queue. Will wait up to duration amount of time, at which point
     * if the object was not published it will return false.
     *
     * @return oldest object in the queue
     * @throws InterruptedException if the thread was interrupted while waiting for an object
     */
    public T consume(Duration timeout) throws InterruptedException {
        final long start = System.nanoTime();
        final long t = timeout.toNanos();
        T entry;
        while ((entry = queue.get()) == null && (System.nanoTime() - start) < t) {
            // don't oversleep
            final long sleep = t - (System.nanoTime() - start) % 100;
            Thread.sleep(sleep);
        }
        return entry;
    }
}
