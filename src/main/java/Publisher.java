import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Publisher of objects to a {@link MessageQueue}. This will block until it can successfully publish
 * the object.
 *
 * @author evanwht1@gmail.com
 */
public class Publisher<T> {

    private final MessageQueue<T> queue;

    Publisher(final MessageQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * Publishes an object to a message queue. Will wait up to the specified amount of time, at which point
     * if the object was not published it will throw a timeout exception.
     *
     * @param object object to publish
     * @throws InterruptedException if the thread was interrupted while waiting to publish to the queue
     */
    public void publish(final T object, Duration timeout) throws InterruptedException, TimeoutException {
        queue.add(object, timeout);
    }

    /**
     * Publishes an object to a message queue. Will wait indefinitely until it can either publish or is interrupted.
     *
     * @param object object to publish
     * @throws InterruptedException if the thread was interrupted while waiting to publish to the queue
     */
    public void publish(final T object) throws InterruptedException {
        queue.add(object);
    }
}
