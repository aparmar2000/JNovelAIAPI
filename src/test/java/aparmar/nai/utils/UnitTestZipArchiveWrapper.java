package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;

class UnitTestZipArchiveWrapper {

	@Test
	void testDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException, IOException {
		ZipArchiveWrapper testInstance1 = new ZipArchiveWrapper(
				IOUtils.toByteArray(InternalResourceLoader.getInternalResourceAsStream("test_zip_1_img.zip"))
				);
		ZipArchiveWrapper testInstance2 = new ZipArchiveWrapper(
				IOUtils.toByteArray(InternalResourceLoader.getInternalResourceAsStream("test_zip_3_imgs.zip"))
				);
		TestHelpers.autoTestDataAndToBuilderAnnotation(ZipArchiveWrapper.class, testInstance1, testInstance2);
	}
	
	@Test
	void testZipParsing() throws FileNotFoundException, IOException {
		ZipArchiveWrapper testZip = new ZipArchiveWrapper(
				IOUtils.toByteArray(InternalResourceLoader.getInternalResourceAsStream("test_zip_3_imgs.zip"))
				);
		
		assertEquals(3, testZip.getEntryCount());
		for (int i=0;i<testZip.getEntryCount();i++) {
			assertNotNull(testZip.getEntry(i));
			assertTrue(testZip.getEntryBytes(i).length>0);
		}
	}

}
