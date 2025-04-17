package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UnitTestByteArrayEncodings {
	
	static Stream<Arguments> inOutPairGenerator() throws FileNotFoundException, IOException {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_nanores.png"));
		LinkedList<Arguments> argumentList = new LinkedList<>();
		

		argumentList.add(Arguments.of(
				"string to byte array", 
				new byte[] {115, 116, 114, 105, 110, 103, 32, 116, 111, 32, 98, 121, 116, 101, 32, 97, 114, 114, 97, 121}, 
				(Function<String,byte[]>)ByteArrayEncodings::stringToByteArray));
		argumentList.add(Arguments.of(
				baseImage, 
				new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 16, 0, 0, 0, 12, 8, 2, 0, 0, 0, -28, -123, -86, -42, 0, 0, 1, -10, 73, 68, 65, 84, 120, -38, 69, 82, 75, 107, 19, 81, 20, -98, 85, 59, 115, 31, -13, -72, -9, -52, -53, -92, 84, -86, 82, 37, 32, 109, 21, 55, -70, 17, 117, -95, 43, 31, -24, 70, -70, 80, -44, 77, -15, -127, -117, -72, -77, 104, -107, 46, 74, -85, 20, -33, 79, 80, 4, -59, 23, 86, 11, 117, 37, -123, -76, 102, -111, 8, 37, -112, -74, -79, -87, -104, -44, -122, 10, -2, 7, -65, 116, 10, 29, -50, 12, -9, -98, -7, -66, -17, 126, -25, -100, 107, 48, -58, 56, 103, 66, 48, -37, -26, -114, 43, 60, 37, -55, -73, -3, -48, 13, 66, -49, 15, 60, 77, 14, 50, -82, 39, -92, -51, -127, 65, 24, 120, -91, -28, 72, 41, 109, 3, -76, 107, 47, -11, -33, -119, 30, -68, -113, 70, 95, -123, -89, 46, -122, -87, -76, 14, 35, -107, -48, 108, -89, -55, 49, 64, -59, 6, 41, -4, 56, 121, 62, 24, 43, 4, -59, -33, 52, -37, -96, -71, 6, -107, -106, -94, 23, -97, -70, 55, 117, 82, 20, 41, -14, 93, 40, 58, -114, 48, 60, -107, 104, -85, 125, 71, -30, -41, -71, 40, 95, -43, -27, 101, -86, -2, -115, 107, -1, 54, -42, -22, -9, 43, -43, -21, 67, -9, -70, -37, -38, 40, -118, 53, 28, 2, 105, 64, 27, -85, 120, -125, -50, 14, 109, -98, -104, -47, -123, 95, 126, -7, 79, -72, -80, 18, 47, 46, 95, -88, 55, 6, 23, 86, 82, -71, 114, 106, -37, 118, -118, 99, 13, 81, -128, -41, 8, 112, 127, -19, 81, -6, 115, -47, -101, -2, 73, 51, 53, 42, -43, 105, 126, 113, 120, 110, 105, 103, -87, 70, 83, -13, -2, -63, 19, 33, 12, -125, 0, 99, 6, 78, 1, 71, 107, -103, -67, 21, -66, -4, -26, 126, 41, -88, -55, -78, -98, -82, -88, -4, -113, 43, -7, 74, -112, -101, -43, -29, 69, -67, -1, -80, -65, 78, 64, -59, 40, 3, 113, -68, -49, 30, 121, 43, 31, -114, -53, -89, 19, -14, -61, 119, 111, -20, -21, -103, -113, 83, -2, -101, 73, 53, -6, -114, -46, -19, 77, 63, 73, -105, 13, 20, -18, -72, -51, -74, 118, 100, 68, -33, 13, 121, -11, 46, 31, 120, 44, 6, -97, -37, -73, -97, -19, -23, 31, -55, -36, 124, -94, -114, -99, -90, 48, -14, -110, -79, 52, -117, -26, -36, 66, 119, 29, 7, 83, -29, -99, 93, -30, -24, 57, -34, 123, -39, -22, -67, 100, -99, -51, -46, -127, 67, -19, 61, -69, -109, 9, -70, -48, 6, -38, -11, -92, -63, -104, -103, 112, -124, 52, -71, 108, 117, -108, -39, -79, -43, -52, -20, -80, -74, 100, 24, 5, 124, -43, -80, 84, -54, -58, 23, -9, 0, 35, 6, 1, -113, -71, 30, -68, -43, 98, 45, -116, -75, 8, 97, -82, 30, 11, -112, 16, 98, -19, 94, 112, -50, -1, 3, 67, -37, -127, -110, 22, 115, -124, 97, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126}, 
				(Function<BufferedImage,byte[]>)ByteArrayEncodings::bufferedImageToByteArray));
		
		argumentList.add(Arguments.of(
				baseImage, 
				"1b30ec5c8abe19fceb1cf168331cc7824500b53f0218cc911522d51ad60d5356", 
				(Function<BufferedImage,String>)ByteArrayEncodings::encodeBufferedImageToSha256Hex));
		argumentList.add(Arguments.of(
				baseImage, 
				"iVBORw0KGgoAAAANSUhEUgAAABAAAAAMCAIAAADkharWAAAB9klEQVR42kVSS2sTURSeVTtzH/O4\r\n"
				+ "98zLpFSqUiUgbRU3uhF1oSsf6Ea6UNRN8YGLuLNolS5KqxTfT1AExRdWC3UlhbRmkQglkLaxqZjU\r\n"
				+ "hgr+B790Ch3ODPee+b7vfuecazDGOGdCMNvmjis8Jcm3/dANQs8PPE0OMq4npM2BQRh4peRIKW0D\r\n"
				+ "tGsv9d+JHryPRl+Fpy6GqbQOI5XQbKfJMUDFBin8OHk+GCsExd8026C5BpWWohefujd1UhQp8l0o\r\n"
				+ "Oo4wPJVoq31H4te5KF/V5WWq/o1r/zbW6vcr1etD97rb2iiKNRwCaUAbq3iDzg5tnpjRhV9++U+4\r\n"
				+ "sBIvLl+oNwYXVlK5cmrbdopjDVGA1whwf+1R+nPRm/5JMzUq1Wl+cXhuaWepRlPz/sETIQyDAGMG\r\n"
				+ "TgFHa5m9Fb785n4pqMmynq6o/I8r+UqQm9XjRb3/sL9OQMUoA3G8zx55Kx+Oy6cT8sN3b+zrmY9T\r\n"
				+ "/ptJNfqO0u1NP0mXDRTuuM22dmRE3w159S4feCwGn9u3n+3pH8ncfKKOnaYw8pKxNIvm3EJ3HQdT\r\n"
				+ "451d4ug53nvZ6r1knc3SgUPtPbuTCbrQBtr1pMGYmXCENLlsdZTZsdXM7LC2ZBgFfNWwVMrGF/cA\r\n"
				+ "IwYBj7kevNViLYy1CGGuHguQEGLtXnDO/wND24GSFnOEYQAAAABJRU5ErkJggg==", 
				(Function<BufferedImage,String>)ByteArrayEncodings::encodeBufferedImageToB64));
		argumentList.add(Arguments.of(
				baseImage, 
				"179f3dff306bcff6ccef127e131a8d84", 
				(Function<BufferedImage,String>)ByteArrayEncodings::encodeBufferedImageToMd5Hex));
		
		argumentList.add(Arguments.of(
				"information_extracted:0.5", 
				"f3bbf4437fab11af7e11de2cba74f564b039906a7cddbea10dcda4f34d65841a", 
				(Function<String,String>)ByteArrayEncodings::encodeStringBytesToSha256Hex));
		argumentList.add(Arguments.of(
				"string to b64", 
				"c3RyaW5nIHRvIGI2NA==", 
				(Function<String,String>)ByteArrayEncodings::encodeStringBytesToB64));
		argumentList.add(Arguments.of(
				"string to md5", 
				"6932692e9e37c56334906bc60e02a621", 
				(Function<String,String>)ByteArrayEncodings::encodeStringBytesToMd5Hex));
		
		return argumentList.stream();
	}

	@ParameterizedTest
	@MethodSource("inOutPairGenerator")
	<A, B> void testInOutPairs(A in, B out, Function<A,B> testFunc) {
		if (out instanceof byte[]) {
			assertArrayEquals((byte[])out, (byte[])testFunc.apply(in));
		} else {
			assertEquals(out, testFunc.apply(in));
		}
	}

}
