package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnitTestChangeReportingWrappedIterator {
    private List<String> testList;
    private AtomicInteger changeCount;
    private ChangeReportingWrappedIterator<Iterator<String>, String> testIterator;

    @BeforeEach
    void setUp() {
        testList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        changeCount = new AtomicInteger(0);
        testIterator = new ChangeReportingWrappedIterator<>(testList.iterator(), changeCount::incrementAndGet);
    }

    @Test
    void testHasNext() {
        assertTrue(testIterator.hasNext());
        testIterator.next();
        assertTrue(testIterator.hasNext());
        testIterator.next();
        assertTrue(testIterator.hasNext());
        testIterator.next();
        assertFalse(testIterator.hasNext());
        assertEquals(0, changeCount.get());
    }

    @Test
    void testNext() {
        assertEquals("a", testIterator.next());
        assertEquals("b", testIterator.next());
        assertEquals("c", testIterator.next());
        assertEquals(0, changeCount.get());
    }

    @Test
    void testRemove() {
    	testIterator.next();
    	testIterator.remove();
        
        assertEquals(1, changeCount.get());
        assertEquals(Arrays.asList("b", "c"), testList);
        
        testIterator.next();
        testIterator.next();
        testIterator.remove();
        
        assertEquals(2, changeCount.get());
        assertEquals(Arrays.asList("b"), testList);
    }

    @Test
    void testForEachRemaining() {
    	testIterator.next();
        
        List<String> remaining = new ArrayList<>();
        Consumer<String> consumer = remaining::add;
        testIterator.forEachRemaining(consumer);
        
        assertEquals(Arrays.asList("b", "c"), remaining);
        assertEquals(0, changeCount.get());
    }
}
