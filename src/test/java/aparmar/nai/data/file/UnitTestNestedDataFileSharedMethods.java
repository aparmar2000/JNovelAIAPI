package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

public abstract class UnitTestNestedDataFileSharedMethods<T extends NestedDataFile<T, D>, D extends DataFile<D>> extends UnitTestDataFileSharedMethods<T> {

    @TempDir
    Path tempDir;

    Path testPath;
    T nestedDataFile;

    @Mock D mockDataFile1;
    @Mock D mockDataFile2;
    @Mock D mockDataFile3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testPath = tempDir.resolve("testNested."+makeEmptyInstance(null).getFileExt());
        nestedDataFile = makeEmptyInstance(testPath);
    }

    // --- Delegated List Method Tests ---

    @Test
    void testSizeAndIsEmpty() {
        assertTrue(nestedDataFile.isEmpty());
        assertEquals(0, nestedDataFile.size());

        nestedDataFile.add(mockDataFile1);
        assertFalse(nestedDataFile.isEmpty());
        assertEquals(1, nestedDataFile.size());

        nestedDataFile.add(mockDataFile2);
        assertEquals(2, nestedDataFile.size());
    }

    @Test
    void testContains() {
        assertFalse(nestedDataFile.contains(mockDataFile1));
        nestedDataFile.add(mockDataFile1);
        
        assertTrue(nestedDataFile.contains(mockDataFile1));
        assertFalse(nestedDataFile.contains(mockDataFile2));
    }

    @Test
    void testIterator() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        Iterator<D> iterator = nestedDataFile.iterator();
        
        assertTrue(iterator.hasNext());
        assertEquals(mockDataFile1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(mockDataFile2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testToArray() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        Object[] array = nestedDataFile.toArray();
        assertArrayEquals(new Object[]{mockDataFile1, mockDataFile2}, array);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testToArrayTyped() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        // Reflective array creation to make generic array.
        D[] typedArray = (D[]) java.lang.reflect.Array.newInstance(mockDataFile1.getClass().getSuperclass(), 2);

        D[] result = nestedDataFile.toArray(typedArray);
        assertSame(typedArray, result);
        assertArrayEquals(new DataFile<?>[]{mockDataFile1, mockDataFile2}, result);

        D[] smallerArray = (D[]) java.lang.reflect.Array.newInstance(mockDataFile1.getClass().getSuperclass(), 1);
        result = nestedDataFile.toArray(smallerArray);
        assertNotSame(smallerArray, result);
        assertArrayEquals(new DataFile<?>[]{mockDataFile1, mockDataFile2}, result);

        D[] largerArray = (D[]) java.lang.reflect.Array.newInstance(mockDataFile1.getClass().getSuperclass(), 3);
        result = nestedDataFile.toArray(largerArray);
        assertSame(largerArray, result);
        assertArrayEquals(new DataFile<?>[]{mockDataFile1, mockDataFile2, null}, result);
    }

    @Test
    void testAdd() {
        assertTrue(nestedDataFile.add(mockDataFile1));
        
        assertEquals(1, nestedDataFile.size());
        assertEquals(mockDataFile1, nestedDataFile.get(0));
    }

    @Test
    void testRemoveObject() {
        nestedDataFile.add(mockDataFile1);
        nestedDataFile.add(mockDataFile2);
        
        assertTrue(nestedDataFile.remove(mockDataFile1));
        
        assertEquals(1, nestedDataFile.size());
        assertFalse(nestedDataFile.contains(mockDataFile1));
        assertTrue(nestedDataFile.contains(mockDataFile2));
        assertFalse(nestedDataFile.remove(mockDataFile3));
    }

    @Test
    void testContainsAll() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        
        assertTrue(nestedDataFile.containsAll(Arrays.asList(mockDataFile1, mockDataFile2)));
        assertTrue(nestedDataFile.containsAll(Arrays.asList(mockDataFile2, mockDataFile1)));
        assertTrue(nestedDataFile.containsAll(Lists.newArrayList(mockDataFile1)));
        assertFalse(nestedDataFile.containsAll(Arrays.asList(mockDataFile1, mockDataFile3)));
        assertTrue(nestedDataFile.containsAll(Collections.emptyList()));
    }

    @Test
    void testAddAllCollection() {
        List<D> toAdd = Arrays.asList(mockDataFile1, mockDataFile2);
        assertTrue(nestedDataFile.addAll(toAdd));
        
        assertEquals(2, nestedDataFile.size());
        assertTrue(nestedDataFile.containsAll(toAdd));
        assertFalse(nestedDataFile.addAll(Collections.emptyList()));
    }

    @Test
    void testAddAllAtIndex() {
        nestedDataFile.add(mockDataFile3);
        
        List<D> toAdd = Arrays.asList(mockDataFile1, mockDataFile2);
        assertTrue(nestedDataFile.addAll(0, toAdd));
        
        assertEquals(3, nestedDataFile.size());
        assertEquals(mockDataFile1, nestedDataFile.get(0));
        assertEquals(mockDataFile2, nestedDataFile.get(1));
        assertEquals(mockDataFile3, nestedDataFile.get(2));
        assertFalse(nestedDataFile.addAll(1, Collections.emptyList()));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRemoveAll() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile3));
        D extraMock = (D) mock(mockDataFile1.getClass());
        List<D> toRemove = Arrays.asList(mockDataFile1, mockDataFile3, extraMock);
        
        assertTrue(nestedDataFile.removeAll(toRemove));
        assertEquals(1, nestedDataFile.size());
        assertFalse(nestedDataFile.contains(mockDataFile1));
        assertTrue(nestedDataFile.contains(mockDataFile2));
        assertFalse(nestedDataFile.contains(mockDataFile3));
        assertFalse(nestedDataFile.removeAll(Lists.newArrayList(mock(mockDataFile1.getClass()))));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRetainAll() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile3));
		D extraMock = (D) mock(mockDataFile1.getClass());
        List<D> toRetain = Arrays.asList(mockDataFile2, mockDataFile3, extraMock);
        
        assertTrue(nestedDataFile.retainAll(toRetain));
        assertEquals(2, nestedDataFile.size());
        assertFalse(nestedDataFile.contains(mockDataFile1));
        assertTrue(nestedDataFile.contains(mockDataFile2));
        assertTrue(nestedDataFile.contains(mockDataFile3));
        assertFalse(nestedDataFile.retainAll(Arrays.asList(mockDataFile2, mockDataFile3)));
    }

    @Test
    void testClear() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        assertFalse(nestedDataFile.isEmpty());
        nestedDataFile.clear();
        assertTrue(nestedDataFile.isEmpty());
        assertEquals(0, nestedDataFile.size());
    }

    @Test
    void testGet() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        assertEquals(mockDataFile1, nestedDataFile.get(0));
        assertEquals(mockDataFile2, nestedDataFile.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.get(-1));
    }

    @Test
    void testSet() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        D previous = nestedDataFile.set(0, mockDataFile3);
        
        assertEquals(mockDataFile1, previous);
        assertEquals(mockDataFile3, nestedDataFile.get(0));
        assertEquals(mockDataFile2, nestedDataFile.get(1));
        assertEquals(2, nestedDataFile.size());
        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.set(2, mockDataFile1));
    }

    @Test
    void testAddAtIndex() {
        nestedDataFile.add(mockDataFile1);
        nestedDataFile.add(0, mockDataFile2); // Add at beginning
        
        assertEquals(2, nestedDataFile.size());
        assertEquals(mockDataFile2, nestedDataFile.get(0));
        assertEquals(mockDataFile1, nestedDataFile.get(1));

        nestedDataFile.add(1, mockDataFile3); // Add in middle
        assertEquals(3, nestedDataFile.size());
        assertEquals(mockDataFile2, nestedDataFile.get(0));
        assertEquals(mockDataFile3, nestedDataFile.get(1));
        assertEquals(mockDataFile1, nestedDataFile.get(2));

        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.add(4, mockDataFile1));
        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.add(-1, mockDataFile1));
    }

    @Test
    void testRemoveAtIndex() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile3));
        D removed = nestedDataFile.remove(1);
        
        assertEquals(mockDataFile2, removed);
        assertEquals(2, nestedDataFile.size());
        assertEquals(mockDataFile1, nestedDataFile.get(0));
        assertEquals(mockDataFile3, nestedDataFile.get(1));

        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.remove(2));
        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.remove(-1));
    }

    @Test
    void testIndexOf() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile1));
        
        assertEquals(0, nestedDataFile.indexOf(mockDataFile1));
        assertEquals(1, nestedDataFile.indexOf(mockDataFile2));
        assertEquals(-1, nestedDataFile.indexOf(mockDataFile3));
    }

    @Test
    void testLastIndexOf() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile1));
        
        assertEquals(2, nestedDataFile.lastIndexOf(mockDataFile1));
        assertEquals(1, nestedDataFile.lastIndexOf(mockDataFile2));
        assertEquals(-1, nestedDataFile.lastIndexOf(mockDataFile3));
    }

    @Test
    void testListIterator() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2));
        ListIterator<D> iterator = nestedDataFile.listIterator();
        
        assertTrue(iterator.hasNext());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals(-1, iterator.previousIndex());
        assertEquals(mockDataFile1, iterator.next());

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        assertEquals(1, iterator.nextIndex());
        assertEquals(0, iterator.previousIndex());
        assertEquals(mockDataFile2, iterator.next());

        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        assertEquals(2, iterator.nextIndex());
        assertEquals(1, iterator.previousIndex());
        assertEquals(mockDataFile2, iterator.previous());

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        assertEquals(1, iterator.nextIndex());
        assertEquals(0, iterator.previousIndex());
    }

    @Test
    void testListIteratorAtIndex() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile3));
        ListIterator<D> iterator = nestedDataFile.listIterator(1);

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        assertEquals(1, iterator.nextIndex());
        assertEquals(0, iterator.previousIndex());
        assertEquals(mockDataFile2, iterator.next());
        assertEquals(mockDataFile3, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testSubList() {
        nestedDataFile.addAll(Arrays.asList(mockDataFile1, mockDataFile2, mockDataFile3, mockDataFile1));
        List<D> subList = nestedDataFile.subList(1, 3);

        assertEquals(2, subList.size());
        assertEquals(mockDataFile2, subList.get(0));
        assertEquals(mockDataFile3, subList.get(1));

        // Test modification through sublist affects original
        subList.set(0, mockDataFile3);
        assertEquals(mockDataFile3, nestedDataFile.get(1));

        // Test modification through original affects sublist
        nestedDataFile.set(2, mockDataFile2);
        assertEquals(mockDataFile2, subList.get(1));

        // Test clearing sublist
        subList.clear();
        assertEquals(2, nestedDataFile.size());
        assertEquals(mockDataFile1, nestedDataFile.get(0));
        assertEquals(mockDataFile1, nestedDataFile.get(1)); // Original index 3 is now 1

        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.subList(0, 3)); // Original size changed
        assertThrows(IndexOutOfBoundsException.class, () -> nestedDataFile.subList(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> nestedDataFile.subList(1, 0));
    }
}
