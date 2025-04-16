package aparmar.nai.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;

public class ByteArrayEncodings {
	public static byte[] bufferedImageToByteArray(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return baos.toByteArray();
	}
	
	public static String encodeByteArrayToB64(byte[] bytes) {
		return Base64.getMimeEncoder().encodeToString(bytes);
	}
	public static String encodeBufferedImageToB64(BufferedImage image) {
		return encodeByteArrayToB64(bufferedImageToByteArray(image));
	}
	
	public static byte[] decodeB64ToByteArray(String base64) {
		return Base64.getMimeDecoder().decode(base64);
	}
	
	
	public static String encodeByteArrayToMd5Hex(byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] hashBytes = md.digest(bytes);
	        return Hex.encodeHexString(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String encodeBufferedImageToMd5Hex(BufferedImage image) {
		return encodeByteArrayToMd5Hex(bufferedImageToByteArray(image));
	}
}
