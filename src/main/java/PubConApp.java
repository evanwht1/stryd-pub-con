import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @author evanwht1@gmail.com
 */
public class PubConApp {

    public static void main(String[] args) {
        MessageQueueFactory<String> messageQueueFactory = new MessageQueueFactory<>(2);

        Publisher<String> publisher = messageQueueFactory.createPublisher();
        Consumer<String> consumer = messageQueueFactory.createConsumer();

        ExecutorService service = Executors.newFixedThreadPool(4);

        Runnable pub = () -> {
            int n = 0;
            while (true) {
                try {
                    publisher.publish("Message " + Thread.currentThread().getName() + " " + n++, Duration.ofMillis(100));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        Runnable con = () -> {
            while (true) {
                try {
                    System.out.println(consumer.consume(Duration.ofMillis(100)));
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        service.execute(pub);
        service.execute(pub);
        service.execute(con);
        service.execute(con);
    }

}
