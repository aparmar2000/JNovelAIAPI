package aparmar.nai.data.request;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aparmar.nai.utils.ByteArrayEncodings;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A thin wrapper around a <code>BufferedImage</code> that handles resizing and serializing to base64 for the request body.
 * </br></br>
 * Uses Bicubic interpolation for resizing normally, but switches to Nearest Neighbor if the image is flagged as a mask.
 * Supports multiple scaling modes.
 */
@Data
@NoArgsConstructor
public class Base64Image implements JsonSerializer<Base64Image>, JsonDeserializer<Base64Image> {
	public static enum ScalingMode {
		STRETCH,
		PAD_BLACK;
	}
	
	protected BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	protected int targetWidth = 1, targetHeight = 1;
	protected ScalingMode scalingMode = ScalingMode.STRETCH;
	protected boolean isMask = false;
	@EqualsAndHashCode.Exclude
	@Getter(value = AccessLevel.NONE)
	private transient AtomicReference<String> imageSha256 = new AtomicReference<String>();
	
	public Base64Image(BufferedImage image) {
		this.image = image;
		targetWidth = image.getWidth();
		targetHeight = image.getHeight();
		isMask = false;
	}

	public Base64Image(BufferedImage image, int width, int height, boolean isMask) {
		this.image = image;
		targetWidth = width;
		targetHeight = height;
		this.isMask = isMask;
	}

	public Base64Image(BufferedImage image, int width, int height, ScalingMode scalingMode, boolean isMask) {
		this.image = image;
		targetWidth = width;
		targetHeight = height;
		this.scalingMode = scalingMode;
		this.isMask = isMask;
	}
	
	public void setImage(BufferedImage image) {
		imageSha256.set(null);
		this.image = image;
		imageSha256.set(null);
	}
	
	public BufferedImage getProcessedImage() {
		BufferedImage processedImage = new BufferedImage(
				getTargetWidth(), getTargetHeight(), 
				BufferedImage.TYPE_INT_RGB);
		int targetWidth = getTargetWidth();
		int targetHeight = getTargetHeight();
		
		Graphics2D g2d = processedImage.createGraphics();
		if (isMask()) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		switch (getScalingMode()) {
		case STRETCH:
		    g2d.drawImage(getImage(), 0, 0, targetWidth, targetHeight, null);
			break;
		case PAD_BLACK:
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, targetWidth, targetHeight);
			
			int imgWidth = getImage().getWidth();
			int imgHeight = getImage().getHeight();

			double scaleX = (double) targetWidth / imgWidth;
			double scaleY = (double) targetHeight / imgHeight;
			double scaleFactor = Math.min(scaleX, scaleY); 

			int scaledWidth = (int) (imgWidth * scaleFactor);
			int scaledHeight = (int) (imgHeight * scaleFactor);

			int x = (targetWidth - scaledWidth) / 2;
			int y = (targetHeight - scaledHeight) / 2;
			
			g2d.drawImage(getImage(), x, y, scaledWidth, scaledHeight, null);
			break;
		}
	    g2d.dispose();
	    
	    return processedImage;
	}

	@Override
	public JsonElement serialize(Base64Image src, Type typeOfSrc, JsonSerializationContext context) {	    
		return new JsonPrimitive(ByteArrayEncodings.encodeBufferedImageToB64(src.getProcessedImage()));
	}

	@Override
	public Base64Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			byte[] imgBytes = ByteArrayEncodings.decodeB64ToByteArray(json.getAsString());
			BufferedImage readImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
			
			return new Base64Image(readImage, readImage.getWidth(), readImage.getHeight(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Base64Image();
	}

    public String generateSha256() {
    	return imageSha256.updateAndGet(v->{
    		if (v!=null) { return v; }            
            return ByteArrayEncodings.encodeBufferedImageToSha256Hex(image);
    	});
    }
}
