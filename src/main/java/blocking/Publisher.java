package blocking;

import java.time.Duration;

/**
 * Publisher of objects to a queue that will block the thread that called it until it can succesfully publish
 * the object or times out.
 *
 * @author evanwht1@gmail.com
 */
public class Publisher<T> {

    private final BlockingBuffer<T> queue;

    public Publisher(final BlockingBuffer<T> queue) {
        this.queue = queue;
    }

    /**
     * Publishes an object to a message queue. Will wait up to duration amount of time, at which point
     * if the object was not published it will return false.
     *
     * @param object object to publish
     * @return whether or not the object was published to the queue
     * @throws InterruptedException if the thread was interrupted while waiting to publish to the queue
     */
    public boolean publish(final T object, Duration timeout) throws InterruptedException {
        final long start = System.nanoTime();
        final long t = timeout.toNanos();
        boolean added;
        while (!(added = queue.add(object)) && (System.nanoTime() - start) < t) {
            // don't oversleep
            final long sleep = t - (System.nanoTime() - start) % 100;
            Thread.sleep(sleep);
        }
        return added;
    }
}
