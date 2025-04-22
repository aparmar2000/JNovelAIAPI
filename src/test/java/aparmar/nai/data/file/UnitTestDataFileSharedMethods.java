package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aparmar.nai.TestHelpers;

abstract class UnitTestDataFileSharedMethods<T extends DataFile<T>> {

	abstract Class<T> getTestedClass();
	abstract String getFileExtension();
	
	abstract T makeInstanceOne(Path path);
	abstract T makeInstanceTwo(Path path);
	abstract T makeEmptyInstance(Path path);

	@Test
	void testDataFileDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		T testInstance1 = makeInstanceOne(new File("dummy1.file").toPath());
		T testInstance2 = makeInstanceTwo(new File("dummy2.file").toPath());
		TestHelpers.autoTestDataAndToBuilderAnnotation(getTestedClass(), testInstance1, testInstance2);
	}
	
	@Test
	void testSaveAndLoad(@TempDir Path tempDir) throws IOException {
		Path testPath = tempDir.resolve("test_save_and_load."+getFileExtension());
		
		T testInstance = makeInstanceOne(testPath);
		testInstance.save();
		
		T testInstance2 = makeEmptyInstance(testPath).load();
		assertEquals(testInstance, testInstance2);
	}
	
	@Test
	void testSaveToFile(@TempDir Path tempDir) throws IOException {
		Path testPath = tempDir.resolve("test_save_as."+getFileExtension());
		
		T testInstance = makeInstanceOne(null).saveToFile(testPath.toFile());
		
		T testInstance2 = makeEmptyInstance(testPath).load();
		assertEquals(testInstance, testInstance2);
	}
	
	@Test
	void testCloneWithNewPath(@TempDir Path tempDir) throws IOException {
		Path testPath = tempDir.resolve("test_clone_as."+getFileExtension());
		T testInstance = makeInstanceOne(null).cloneWithNewPath(testPath);
		
		T testInstance2 = makeInstanceOne(testPath);
		assertEquals(testInstance, testInstance2);
	}
	
	@Test
	void testSaveLoadNullPath() throws IOException {
		T testInstance = makeEmptyInstance(null);
		assertThrows(UnsupportedOperationException.class, ()->testInstance.save());
		assertThrows(UnsupportedOperationException.class, ()->testInstance.load());
	}
}
