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
				new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 16, 0, 0, 0, 12, 8, 2, 0, 0, 0, -28, -123, -86, -42, 0, 0, 1, -12, 73, 68, 65, 84, 120, 94, 69, 82, 75, 107, 19, 81, 20, -98, 85, 59, 115, 31, -13, -72, -9, -52, -53, -92, 84, -86, 82, 75, 64, -84, -118, 27, -35, -120, -70, -48, -107, 15, 116, 35, 93, 40, -22, -90, 84, -91, -117, -72, -77, -8, -94, -117, 98, -107, -30, -5, 9, -118, -96, -8, -62, 106, -95, -82, -92, -112, -42, 44, 18, -95, 4, -46, 54, 54, 21, -109, -38, 80, -63, -1, -32, 55, 38, -48, -31, -52, 112, -17, -103, -17, -5, -18, 119, -50, -71, 6, 99, -116, 115, 38, 4, -77, 109, -18, -72, -62, 83, -110, 124, -37, 15, -35, 32, -12, -4, -64, -45, -28, 32, -29, 122, 66, -38, 28, 24, -124, -127, 87, 74, -114, -108, -46, 54, 64, 59, -9, -48, -48, -19, -24, -2, -69, 104, -20, 101, 120, -14, 124, -104, 74, -21, 48, 82, 77, -102, -19, 36, 28, 3, 84, 108, -112, -62, -113, 19, 3, -63, 120, 33, 40, -2, -94, -71, 6, -51, 55, -88, -76, 28, 61, -1, -40, -69, -95, -101, -94, 72, -111, -17, 66, -47, 113, -124, -31, -87, -90, -74, -38, 123, 56, 126, -107, -117, -14, 85, 93, 94, -95, -22, -97, -72, -10, 119, 125, -83, 126, -81, 82, -67, 50, 114, -73, -73, -93, -125, -94, 88, -61, 33, -112, 6, -76, -79, -118, -41, -23, -20, -56, -58, -55, 89, 93, -8, -23, -105, 127, -121, -117, -85, -15, -46, -54, -71, 122, 99, 120, 113, 53, -107, 43, -89, 122, -74, 80, 28, 107, -120, 2, -36, 34, -64, -3, -27, -121, -23, 79, 69, 111, -26, 7, -51, -42, -88, 84, -89, -123, -91, 27, -13, -53, 59, 74, 53, -102, 94, -16, 15, 28, 15, 97, 24, 4, 24, 51, 112, 10, 56, 90, -53, -20, -51, -16, -59, 87, -9, 115, 65, 77, -107, -11, 76, 69, -27, -65, 95, -52, 87, -126, -36, -100, -98, 40, -22, 125, -121, -4, 53, 2, 42, 70, 25, -120, 99, -3, -10, -24, 27, -7, 96, 66, 62, -103, -108, -17, -65, 121, -29, 95, 78, 127, -104, -10, 95, 79, -87, -79, -73, -108, -18, 76, -4, 52, -69, 108, -96, 112, -57, 77, -38, -38, -107, 17, -3, -41, -28, -91, 59, -4, -22, 35, 49, -4, -52, -66, -11, 116, -9, -48, 104, -26, -6, 99, 117, -12, 20, -123, 81, -30, -71, 85, 52, -25, 22, -70, -21, 56, -104, 26, -17, -34, 42, -114, -100, -27, 125, -125, 86, -33, 5, -21, 76, -106, -10, 31, -20, -36, -74, -85, 57, 65, 23, -38, 64, -69, -98, 52, 24, 51, -101, 28, 33, 77, 46, -37, 29, 101, 118, 109, 54, 51, -37, -83, 77, 25, 70, 65, 50, 34, -124, 82, 54, -66, -72, 7, 24, 49, 8, 120, -52, -75, -32, -19, 22, 107, 99, -84, 77, 8, -13, -1, -79, 0, 9, 33, 90, -9, -126, 115, -2, 15, 67, -37, -127, -110, 53, -103, -111, 107, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126}, 
				(Function<BufferedImage,byte[]>)ByteArrayEncodings::bufferedImageToByteArray));
		
		argumentList.add(Arguments.of(
				baseImage, 
				"3fa4e97e2f340ad5d07950ca32c17513ce5d8bb7db4f1cdd2b9b161e5c6d5187", 
				(Function<BufferedImage,String>)ByteArrayEncodings::encodeBufferedImageToSha256Hex));
		argumentList.add(Arguments.of(
				baseImage, 
				"iVBORw0KGgoAAAANSUhEUgAAABAAAAAMCAIAAADkharWAAAB9ElEQVR4XkVSS2sTURSeVTtzH/O4\r\n"
				+ "98zLpFSqUktArIob3Yi60JUPdCNdKOqmVKWLuLP4ootileL7CYqg+MJqoa6kkNYsEqEE0jY2FZPa\r\n"
				+ "UMH/4Dcm0OHMcO+Z7/vud865BmOMcyYEs23uuMJTknzbD90g9PzA0+Qg43pC2hwYhIFXSo6U0jZA\r\n"
				+ "O/fQ0O3o/rto7GV48nyYSuswUk2a7SQcA1RskMKPEwPBeCEo/qK5Bs03qLQcPf/Yu6GbokiR70LR\r\n"
				+ "cYThqaa22ns4fpWL8lVdXqHqn7j2d32tfq9SvTJyt7ejg6JYwyGQBrSxitfp7MjGyVld+OmXf4eL\r\n"
				+ "q/HSyrl6Y3hxNZUrp3q2UBxriALcIsD95YfpT0Vv5gfN1qhUp4WlG/PLO0o1ml7wDxwPYRgEGDNw\r\n"
				+ "Cjhay+zN8MVX93NBTZX1TEXlv1/MV4LcnJ4o6n2H/DUCKkYZiGP99ugb+WBCPpmU7795419Of5j2\r\n"
				+ "X0+psbeU7kz8NLtsoHDHTdralRH91+SlO/zqIzH8zL71dPfQaOb6Y3X0FIVR4rlVNOcWuus4mBrv\r\n"
				+ "3iqOnOV9g1bfBetMlvYf7Ny2qzlBF9pAu540GDObHCFNLtsdZXZtNjPbrU0ZRkEyIoRSNr64Bxgx\r\n"
				+ "CHjMteDtFmtjrE0I8/+xAAkhWveCc/4PQ9uBkjWZkWsAAAAASUVORK5CYII=", 
				(Function<BufferedImage,String>)ByteArrayEncodings::encodeBufferedImageToB64));
		argumentList.add(Arguments.of(
				baseImage, 
				"6b35d65b5fec8937009e17f93008f5ed", 
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
