package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UnitTestChangeReportingWrappedList {
    private List<String> wrappedList;
    private AtomicInteger changeCount;
    private ChangeReportingWrappedList<List<String>, String> testList;

    @BeforeEach
    void setUp() {
        wrappedList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        changeCount = new AtomicInteger(0);
        testList = new ChangeReportingWrappedList<>(wrappedList, changeCount::incrementAndGet);
    }

    @Nested
    class ReadOnlyOperations {
        @Test
        void testSize() {
            assertEquals(3, testList.size());
            assertEquals(0, changeCount.get());
        }

        @Test
        void testIsEmpty() {
            assertFalse(testList.isEmpty());
            assertEquals(0, changeCount.get());
            testList.clear();
            changeCount.set(0);
            assertTrue(testList.isEmpty());
            assertEquals(0, changeCount.get());
        }

        @Test
        void testContains() {
            assertTrue(testList.contains("b"));
            assertFalse(testList.contains("d"));
            assertEquals(0, changeCount.get());
        }

        @Test
        void testToArray() {
            Object[] array = testList.toArray();
            assertEquals(3, array.length);
            assertEquals("b", array[1]);
            assertEquals(0, changeCount.get());
        }

        @Test
        void testToArrayWithArg() {
            String[] array = testList.toArray(new String[0]);
            assertEquals(3, array.length);
            assertEquals("b", array[1]);
            assertEquals(0, changeCount.get());
        }

        @Test
        void testContainsAll() {
            assertTrue(testList.containsAll(Arrays.asList("a", "c")));
            assertFalse(testList.containsAll(Arrays.asList("a", "d")));
            assertEquals(0, changeCount.get());
        }

        @Test
        void testGet() {
            assertEquals("b", testList.get(1));
            assertEquals(0, changeCount.get());
        }

        @Test
        void testIndexOf() {
            assertEquals(1, testList.indexOf("b"));
            assertEquals(-1, testList.indexOf("d"));
            assertEquals(0, changeCount.get());
        }

        @Test
        void testLastIndexOf() {
            testList.add("b");
            changeCount.set(0);
            assertEquals(3, testList.lastIndexOf("b"));
            assertEquals(-1, testList.lastIndexOf("d"));
            assertEquals(0, changeCount.get());
        }
    }

    @Nested
    class WriteOperations {
        @Test
        void testAdd() {
            assertTrue(testList.add("d"));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "b", "c", "d"), wrappedList);
        }

        @Test
        void testAddAtIndex() {
            testList.add(1, "d");
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "d", "b", "c"), wrappedList);
        }

        @Test
        void testRemoveObject() {
            assertTrue(testList.remove("b"));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "c"), wrappedList);

            assertFalse(testList.remove("d"));
            assertEquals(1, changeCount.get());
        }

        @Test
        void testRemoveIndex() {
            assertEquals("b", testList.remove(1));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "c"), wrappedList);
        }

        @Test
        void testAddAll() {
            Collection<String> toAdd = Arrays.asList("d", "e");
            assertTrue(testList.addAll(toAdd));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "b", "c", "d", "e"), wrappedList);

            assertFalse(testList.addAll(Collections.emptyList()));
            assertEquals(1, changeCount.get());
        }

        @Test
        void testAddAllAtIndex() {
            Collection<String> toAdd = Arrays.asList("d", "e");
            assertTrue(testList.addAll(1, toAdd));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "d", "e", "b", "c"), wrappedList);

            assertFalse(testList.addAll(1, Collections.emptyList()));
            assertEquals(1, changeCount.get());
        }

        @Test
        void testRemoveAll() {
            Collection<String> toRemove = Arrays.asList("b", "d");
            assertTrue(testList.removeAll(toRemove));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "c"), wrappedList);

            assertFalse(testList.removeAll(Collections.singleton("e")));
            assertEquals(1, changeCount.get());
        }

        @Test
        void testRetainAll() {
            Collection<String> toRetain = Arrays.asList("a", "c", "d");
            assertTrue(testList.retainAll(toRetain));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "c"), wrappedList);

            assertFalse(testList.retainAll(Arrays.asList("a", "c")));
            assertEquals(1, changeCount.get());
        }

        @Test
        void testClear() {
            testList.clear();
            assertEquals(1, changeCount.get());
            assertTrue(wrappedList.isEmpty());

            testList.clear();
            assertEquals(1, changeCount.get());
        }

        @Test
        void testSet() {
            assertEquals("b", testList.set(1, "d"));
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "d", "c"), wrappedList);
        }
    }

    @Nested
    class WrappedReturnTypes {
        @Test
        void testIterator() {
            java.util.Iterator<String> it = testList.iterator();
            it.next();
            it.remove();
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("b", "c"), wrappedList);
        }

        @Test
        void testListIterator() {
            ListIterator<String> it = testList.listIterator();
            it.next();
            it.add("d");
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "d", "b", "c"), wrappedList);
        }

        @Test
        void testListIteratorWithIndex() {
            ListIterator<String> it = testList.listIterator(1);
            it.next();
            it.set("d");
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "d", "c"), wrappedList);
        }

        @Test
        void testSubList() {
            List<String> subList = testList.subList(1, 3);
            
            subList.remove("b");
            assertEquals(1, changeCount.get());
            assertEquals(Arrays.asList("a", "c"), wrappedList);
            
            testList.add("d");
            assertEquals(2, changeCount.get());
            assertEquals(Arrays.asList("a", "c", "d"), wrappedList);
        }
    }
}
