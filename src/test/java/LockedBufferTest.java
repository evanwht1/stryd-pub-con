import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

/**
 * @author evanwht1@gmail.com
 */
public class LockedBufferTest {

    @Test(expected = TimeoutException.class)
    public void testAddToFull() throws TimeoutException, InterruptedException {
        final MessageQueue<String> buffer = new LockedBuffer<>(2);
        buffer.add("First");
        buffer.add("Second");
        buffer.add("third", Duration.ofMillis(10));
    }

    @Test
    public void testAdd() throws InterruptedException {
        final MessageQueue<String> buffer = new LockedBuffer<>(2);
        buffer.add("First");
        buffer.add("Second");
        try {
            buffer.add("third", Duration.ofMillis(10));
        } catch (TimeoutException e) {
            assertEquals(2, buffer.size());
            buffer.get();
            assertEquals(1, buffer.size());
        }
        buffer.add("third");
        assertEquals(2, buffer.size());
    }

    @Test
    public void testGetOrder() throws InterruptedException {
        final MessageQueue<String> buffer = new LockedBuffer<>(2);
        buffer.add("First");
        buffer.add("Second");
        assertEquals("First", buffer.get());
        buffer.add("Third");
        assertEquals("Second", buffer.get());
    }

    @Test(expected = TimeoutException.class)
    public void testGetEmpty() throws InterruptedException, TimeoutException {
        final MessageQueue<String> buffer = new LockedBuffer<>(2);
        buffer.get(Duration.ofMillis(10));
    }
}
