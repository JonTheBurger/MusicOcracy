package domain.util;

import com.musicocracy.fpgk.domain.util.CircularQueue;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CircularQueueTests {
    @Test(expected = IllegalArgumentException.class)
    public void CircularQueue_constructor_zero_exception() {
        CircularQueue<String> cq = new CircularQueue<>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void CircularQueue_constructor_negative_exception() {
        CircularQueue<String> cq = new CircularQueue<>(-1);
    }

    @Test
    public void CircularQueue_enqueue_sizeIncrements() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        cq.enqueue("A");

        assertEquals(1, cq.size());
    }

    @Test
    public void CircularQueue_enqueue_queueSizeOne() {
        CircularQueue<String> cq = new CircularQueue<>(1);

        cq.enqueue("A");

        assertEquals(1, cq.size());
    }

    @Test
    public void CircularQueue_enqueue_whenFull_SizeSame() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        for (int i = 0; i < 6; i++) {
            cq.enqueue("A");
        }

        assertEquals(5, cq.size());
    }

    @Test
    public void CircularQueue_enqueue_justBeforeFull_SizeSame() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        for (int i = 0; i < 4; i++) {
            cq.enqueue("A");
        }

        assertEquals(4, cq.size());
    }

    @Test
    public void CircularQueue_enqueue_simple_dequeue() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        cq.enqueue("A");

        String returnedString = cq.dequeue();

        assertEquals("A", returnedString);
    }

    @Test
    public void CircularQueue_enqueue_dequeue_queueSizeOne() {
        CircularQueue<String> cq = new CircularQueue<>(1);

        cq.enqueue("A");

        assertEquals("A", cq.dequeue());
    }

    @Test
    public void CircularQueue_enqueue_enqueue_dequeue_queueSizeOne() {
        CircularQueue<String> cq = new CircularQueue<>(1);

        cq.enqueue("A");
        cq.enqueue("B");

        assertEquals("B", cq.dequeue());
    }

    @Test
    public void CircularQueue_enqueuePastMax_dequeueAll_sizeDoesNotChange() {
        String[] inputStrings = new String[]{"A", "B", "C", "D", "E", "F"};

        int queueSize = 5;

        CircularQueue<String> cq = new CircularQueue<>(queueSize);

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        for (int i = 0; i < queueSize; i++) {
            cq.dequeue();
        }

        assertEquals(5, cq.size());
    }

    @Test
    public void CircularQueue_enqueuePastMax_dequeueAll() {
        String[] inputStrings = new String[]{"A", "B", "C", "D", "E", "F"};
        String[] expectedStrings = new String[]{"B", "C", "D", "E", "F"};

        int queueSize = 5;

        CircularQueue<String> cq = new CircularQueue<>(queueSize);

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        for (int i = 0; i < queueSize; i++) {
            assertEquals(expectedStrings[i], cq.dequeue());
        }
    }

    @Test
    public void CircularQueue_enqueuePastMax_dequeuePastAll() {
        String[] inputStrings = new String[]{"A", "B", "C", "D", "E", "F"};
        String[] expectedStrings = new String[]{"B", "C", "D", "E", "F", "B", "C"};

        int queueSize = 5;

        CircularQueue<String> cq = new CircularQueue<>(queueSize);

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        for (int i = 0; i < 7; i++) {
            assertEquals(expectedStrings[i], cq.dequeue());
        }
    }

    @Test
    public void CircularQueue_enqueuePartial_dequeueAll_enqueuePartial_checkDequeue() {
        String[] inputStrings = new String[]{"A", "B", "C"};

        int queueSize = 5;

        CircularQueue<String> cq = new CircularQueue<>(queueSize);

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        for (String ignored : inputStrings) {
            cq.dequeue();
        }

        cq.enqueue("D");
        assertEquals("A", cq.dequeue());
    }

    @Test
    public void CircularQueue_enqueuePastMax_dequeueAll_enqueuePartial_dequeAll_sizeIsMax() {
        int queueSize = 5;

        CircularQueue<String> cq = new CircularQueue<>(queueSize);

        for (int i = 0; i < 6; i++) {
            cq.enqueue("A");
        }

        for (int i = 0; i < 5; i++) {
            cq.dequeue();
        }

        cq.enqueue("B");
        cq.enqueue("C");

        cq.dequeue();
        cq.dequeue();

        assertEquals(queueSize, cq.size());
    }

    @Test
    public void CircularQueue_enqueuePastMax_dequeueAll_enqueuePartial_dequeAll_correctData() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        for (int i = 0; i < 6; i++) {
            cq.enqueue("A");
        }

        for (int i = 0; i < 5; i++) {
            cq.dequeue();
        }

        String[] inputStrings = new String[]{"B", "C", "D"};
        String[] expectedStrings = new String[]{"A", "A", "B"};

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        for (String expectedString : expectedStrings) {
            assertEquals(expectedString, cq.dequeue());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void CircularQueue_dequeueWhenEmpty_IllegalArguementException() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        cq.dequeue();
    }

    @Test
    public void CircularQueue_contains_empty_false() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        assertFalse(cq.contains("A"));
    }

    @Test
    public void CircularQueue_contains_oneEnqueue_true() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        cq.enqueue("A");

        assertTrue(cq.contains("A"));
    }

    @Test
    public void CircularQueue_contains_enqueueWrapAroundOverwrite_false() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        String[] inputStrings = new String[]{"A", "B", "C", "D", "E", "F"};

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        assertFalse(cq.contains("A"));
    }

    @Test
    public void CircularQueue_containsAllButOverwritten_enqueueWrapAroundOverwrite_true() {
        CircularQueue<String> cq = new CircularQueue<>(5);

        String[] inputStrings = new String[]{"A", "B", "C", "D", "E", "F"};

        for (String inputString : inputStrings) {
            cq.enqueue(inputString);
        }

        for (int i = 1; i < inputStrings.length; i++) {
            assertTrue(cq.contains(inputStrings[i]));
        }
    }
}
