/**
 * @author evanwht1@gmail.com
 */
public class MessageQueueFactory<T> {

    private final MessageQueue<T> buffer;

    public MessageQueueFactory(int numMessages) {
        buffer = new LockedBuffer<>(numMessages);
    }

    public Publisher<T> createPublisher() {
        return new Publisher<>(buffer);
    }

    public Consumer<T> createConsumer() {
        return new Consumer<>(buffer);
    }
}
