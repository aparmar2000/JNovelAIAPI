package aparmar.nai.data.request;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A thin wrapper around a <code>BufferedImage</code> that handles resizing and serializing to base64 for the request body.
 * </br></br>
 * Uses Bicubic interpolation for resizing normally, but switches to Nearest Neighbor if the image is flagged as a mask.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Base64Image implements JsonSerializer<Base64Image>, JsonDeserializer<Base64Image> {
	protected BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	protected int targetWidth = 1, targetHeight = 1;
	protected boolean isMask = false;
	
	public Base64Image(BufferedImage image) {
		this.image = image;
		targetWidth = image.getWidth();
		targetHeight = image.getHeight();
		isMask = false;
	}

	@Override
	public JsonElement serialize(Base64Image src, Type typeOfSrc, JsonSerializationContext context) {
		try {
			BufferedImage tempImage = new BufferedImage(
					src.getTargetWidth(), src.getTargetHeight(), 
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = tempImage.createGraphics();
			if (src.isMask()) {
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			} else {
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			}
		    g2d.drawImage(src.getImage(), 0, 0, src.getTargetWidth(), src.getTargetHeight(), null);
		    g2d.dispose();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(tempImage, "png", baos);
			
			String encodedImage = Base64.getMimeEncoder().encodeToString(baos.toByteArray());			
			return new JsonPrimitive(encodedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new JsonPrimitive("");
	}

	@Override
	public Base64Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			byte[] imgBytes = Base64.getMimeDecoder().decode(json.getAsString());
			BufferedImage readImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
			
			return new Base64Image(readImage, readImage.getWidth(), readImage.getHeight(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Base64Image();
	}
}
