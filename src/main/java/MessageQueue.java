import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * @author evanwht1@gmail.com
 */
public interface MessageQueue<T> {

    int size();

    /**
     * Adds an entry to the message queue, waiting for as long as necessary or until the thread
     * gets interrupted.
     * @param t object to add
     * @throws InterruptedException if this thread was interrupted while adding or waiting
     */
    void add(final T t) throws InterruptedException;

    /**
     * Sames as {@link MessageQueue#add(Object)} but will only wait for the specified duration.
     * @param t object to add
     * @param timeout amount of time to wait
     * @throws InterruptedException if this thread was interrupted while adding or waiting
     * @throws TimeoutException if the buffer was still after waiting for the configured time
     */
    void add(final T t, Duration timeout) throws InterruptedException, TimeoutException;

    /**
     * Gets the oldest object from the buffer. If the buffer is empty this will wait for as long as necessary
     * for something to be put into the buffer.
     * @return oldest object in the buffer
     * @throws InterruptedException if this thread was interrupted while getting or waiting
     */
    T get() throws InterruptedException;

    /**
     * Sames as {@link MessageQueue#get()} but will only wait for the specified duration.
     * @param timeout amount of time to wait
     * @return oldest object in the buffer
     * @throws InterruptedException if this thread was interrupted while getting or waiting
     * @throws TimeoutException if the buffer was still empty after waiting for the configured time
     */
    T get(Duration timeout) throws InterruptedException, TimeoutException;
}
