package blocking;

import java.util.concurrent.BlockingQueue;

/**
 * @author evanwht1@gmail.com
 */
public class MessageQueueFactory<T> {

    private final BlockingBuffer<T> buffer;

    public MessageQueueFactory(int numMessages) {
        buffer = new BlockingBuffer<>(numMessages);
    }

    public Publisher<T> createPublisher() {
        return new Publisher<>(buffer);
    }

    public Consumer<T> createConsumer() {
        return new Consumer<>(buffer);
    }
}
