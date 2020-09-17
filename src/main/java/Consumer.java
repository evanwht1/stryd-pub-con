import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Consumer of objects from a {@link MessageQueue}. This will block until an object is available or
 * it times out.
 *
 * @author evanwht1@gmail.com
 */
public class Consumer<T> {

    private final MessageQueue<T> queue;

    Consumer(final MessageQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * Consumes an object from a message queue. Will wait up to the specified amount of time, at which point
     * if no object was retrieved it will throw a timeout exception.
     *
     * @return oldest object in the queue
     * @param timeout amount of time to wait for an object
     * @throws InterruptedException if the thread was interrupted while waiting for an object
     */
    public T consume(Duration timeout) throws InterruptedException, TimeoutException {
        return queue.get(timeout);
    }

    /**
     * Consumes an object from a message queue. Will wait indefinitely until an object is available or it interrupted.
     *
     * @return oldest object in the queue
     * @throws InterruptedException if the thread was interrupted while waiting for an object
     */
    public T consume() throws InterruptedException {
        return queue.get();
    }
}
