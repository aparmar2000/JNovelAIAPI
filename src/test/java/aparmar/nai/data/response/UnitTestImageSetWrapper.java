package aparmar.nai.data.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Iterators;

import aparmar.nai.utils.ZipArchiveWrapper;

class UnitTestImageSetWrapper {
	AutoCloseable mocksAutoCloseable;
	
	@Mock
	ZipArchiveWrapper mockZipWrapper;
	@Mock
	ZipArchiveEntry mockZipEntry;
	
	@BeforeEach
	void setUp() {
		mocksAutoCloseable = MockitoAnnotations.openMocks(this);
		
		when(mockZipEntry.getName()).thenReturn("test.png");
		when(mockZipWrapper.getEntryCount()).thenReturn(1);
		when(mockZipWrapper.getEntry(anyInt())).thenReturn(mockZipEntry);
	}
	
	@AfterEach
	void tearDown() throws Exception {
		mocksAutoCloseable.close();
	}
	
	
	@Test
	void testLoadingValidZip() throws IOException {
		ImageSetWrapper testImageSetWrapper = new ImageSetWrapper(mockZipWrapper);
		assertEquals(1, testImageSetWrapper.getImageCount());
	}

	@Test
	void testLoadingInvalidZip() {
		when(mockZipEntry.getName()).thenReturn("test.notpng");
		
		assertThrows(UnsupportedEncodingException.class, ()->new ImageSetWrapper(mockZipWrapper));
	}
	
	@Test
	void testImageParsing() throws IOException {
		ImageSetWrapper testImageSetWrapper = new ImageSetWrapper(mockZipWrapper);
		
		assertThrows(ArrayIndexOutOfBoundsException.class, ()->testImageSetWrapper.getImage(1));


		when( mockZipWrapper.getEntryBytes(anyInt()) ).thenReturn(new byte[0]);
    	ImageReader mockImageReader = mock(ImageReader.class);
    	IIOImage mockIIOImage = mock(IIOImage.class);
    	when( mockImageReader.readAll(anyInt(), any()) ).thenReturn(mockIIOImage);
    	
	    try (MockedStatic<ImageIO> mockImageIO = Mockito.mockStatic(ImageIO.class)) {
	    	mockImageIO.when(()->ImageIO.getImageReadersByFormatName(any())).thenReturn(Iterators.singletonIterator(mockImageReader));
	    	
	    	IIOImage result = testImageSetWrapper.getImage(0);
	    	
	    	assertSame(mockIIOImage, result);
	    }
	    
    	IIOImage result = testImageSetWrapper.getImage(0);
    	assertSame(mockIIOImage, result);
    	verify(mockImageReader, times(1)).readAll(anyInt(), any());
	}

}
