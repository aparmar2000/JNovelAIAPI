package aparmar.nai.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
			e.printStackTrace();// Should never happen
		}
        return baos.toByteArray();
	}
	public static byte[] stringToByteArray(String string) {
        try {
			return string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();// Should never happen
		}
        return new byte[0];
	}
	
	public static String encodeByteArrayToB64(byte[] bytes) {
		return Base64.getMimeEncoder().encodeToString(bytes);
	}
	public static String encodeBufferedImageToB64(BufferedImage image) {
		return encodeByteArrayToB64(bufferedImageToByteArray(image));
	}
	public static String encodeStringBytesToB64(String string) {
		return encodeByteArrayToB64(stringToByteArray(string));
	}
	
	public static byte[] decodeB64ToByteArray(String base64) {
		return Base64.getMimeDecoder().decode(base64);
	}
	
	
	public static String encodeByteArrayWithDigest(byte[] bytes, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = md.digest(bytes);
        return Hex.encodeHexString(hashBytes);
	}	
	
	public static String encodeByteArrayToMd5Hex(byte[] bytes) {
		try {
	        return encodeByteArrayWithDigest(bytes, "MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();// Should never happen
		}
		return null;
	}
	public static String encodeBufferedImageToMd5Hex(BufferedImage image) {
		return encodeByteArrayToMd5Hex(bufferedImageToByteArray(image));
	}
	public static String encodeStringBytesToMd5Hex(String string) {
		return encodeByteArrayToMd5Hex(stringToByteArray(string));
	}
	
	public static String encodeByteArrayToSha256Hex(byte[] bytes) {
		try {
	        return encodeByteArrayWithDigest(bytes, "SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();// Should never happen
		}
		return null;
	}
	public static String encodeBufferedImageToSha256Hex(BufferedImage image) {
		return encodeByteArrayToSha256Hex(bufferedImageToByteArray(image));
	}
	public static String encodeStringBytesToSha256Hex(String string) {
		return encodeByteArrayToSha256Hex(stringToByteArray(string));
	}
}
