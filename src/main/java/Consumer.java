import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Consumer of objects from a queue that will block the thread that called until an object is available or
 * it times out.
 *
 * @author evanwht1@gmail.com
 */
public class Consumer<T> {

    private final MessageQueue<T> queue;

    public Consumer(final MessageQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * Publishes an object to a message queue. Will wait up to duration amount of time, at which point
     * if the object was not published it will return false.
     *
     * @return oldest object in the queue
     * @throws InterruptedException if the thread was interrupted while waiting for an object
     */
    public T consume(Duration timeout) throws InterruptedException, TimeoutException {
        return queue.get(timeout);
    }
}
