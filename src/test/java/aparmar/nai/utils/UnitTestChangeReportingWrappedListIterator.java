package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnitTestChangeReportingWrappedListIterator {
    private List<String> testList;
    private AtomicInteger changeCount;
    private ChangeReportingWrappedListIterator<ListIterator<String>, String> testListIterator;

    @BeforeEach
    void setUp() {
        testList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        changeCount = new AtomicInteger(0);
        testListIterator = new ChangeReportingWrappedListIterator<>(testList.listIterator(), changeCount::incrementAndGet);
    }

    @Test
    void testRemove() {
        testListIterator.next();
        testListIterator.remove();
        
        assertEquals(1, changeCount.get());
        assertEquals(Arrays.asList("b", "c"), testList);
    }

    @Test
    void testHasPrevious() {
        assertFalse(testListIterator.hasPrevious());
        testListIterator.next();
        assertTrue(testListIterator.hasPrevious());
        assertEquals(0, changeCount.get());
    }

    @Test
    void testPrevious() {
        testListIterator.next();
        testListIterator.next();
        assertEquals("b", testListIterator.previous());
        assertEquals("a", testListIterator.previous());
        assertEquals(0, changeCount.get());
    }

    @Test
    void testNextIndex() {
        assertEquals(0, testListIterator.nextIndex());
        testListIterator.next();
        assertEquals(1, testListIterator.nextIndex());
        assertEquals(0, changeCount.get());
    }

    @Test
    void testPreviousIndex() {
        assertEquals(-1, testListIterator.previousIndex());
        testListIterator.next();
        assertEquals(0, testListIterator.previousIndex());
        assertEquals(0, changeCount.get());
    }

    @Test
    void testSet() {
        testListIterator.next();
        testListIterator.set("z");
        
        assertEquals(1, changeCount.get());
        assertEquals(Arrays.asList("z", "b", "c"), testList);
        
        testListIterator.next();
        testListIterator.set("y");
        
        assertEquals(2, changeCount.get());
        assertEquals(Arrays.asList("z", "y", "c"), testList);
    }

    @Test
    void testAdd() {
        testListIterator.add("x");
        
        assertEquals(1, changeCount.get());
        assertEquals(Arrays.asList("x", "a", "b", "c"), testList);
        assertEquals("a", testListIterator.next());
        
        testListIterator.add("y");
        assertEquals(2, changeCount.get());
        assertEquals(Arrays.asList("x", "a", "y", "b", "c"), testList);
    }
}
