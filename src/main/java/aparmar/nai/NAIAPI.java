package aparmar.nai;

import static aparmar.nai.utils.HelperConstants.AUTH_HEADER;
import static aparmar.nai.utils.HelperConstants.GENERAL_API_ROOT;
import static aparmar.nai.utils.HelperConstants.IMAGE_API_ROOT;
import static aparmar.nai.utils.HelperConstants.MEDIA_TYPE_JSON;
import static aparmar.nai.utils.HelperConstants.PERSISTENT_KEY_PATTERN;

import java.io.IOException;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import aparmar.nai.data.request.IQueryStringPayload;
import aparmar.nai.data.request.ImageAnnotateRequest;
import aparmar.nai.data.request.ImageUpscaleRequest;
import aparmar.nai.data.request.ImageVibeEncodeRequest;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.VoiceGenerationRequest;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRemoveBackgroundRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRequestSingleResult;
import aparmar.nai.data.request.textgen.TextGenerationRequest;
import aparmar.nai.data.response.AudioWrapper;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.data.response.ImageSetWrapperRemoveBackground;
import aparmar.nai.data.response.SingleImageWrapper;
import aparmar.nai.data.response.TextGenerationResponse;
import aparmar.nai.data.response.TextGenerationResponse.LogProbStep;
import aparmar.nai.data.response.TooManyRequestsException;
import aparmar.nai.data.response.UserData;
import aparmar.nai.data.response.UserInfo;
import aparmar.nai.data.response.UserKeystore;
import aparmar.nai.data.response.UserPriority;
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.utils.BuilderAssemblyFunction;
import aparmar.nai.utils.GsonProvider;
import aparmar.nai.utils.RateLimitInterceptor;
import aparmar.nai.utils.ResultParseFunction;
import aparmar.nai.utils.ZipArchiveWrapper;
import aparmar.nai.utils.ZipParseFunction;
import aparmar.nai.utils.tokenization.TokenizedChunk;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NAIAPI {
	private static final RateLimitInterceptor sharedRateLimiter = new RateLimitInterceptor(1);
	private static final Gson gson = GsonProvider.buildGsonInstance();
	
	private final OkHttpClient client;
	
	private final String accessToken;
	
	public NAIAPI(String accessToken) {
		if (!PERSISTENT_KEY_PATTERN.matcher(accessToken).matches()) {
			throw new IllegalArgumentException("Access token \'"+accessToken+"\' appears to be incorrect!");
		}
		this.accessToken = formatAccessToken(accessToken);
		
		client = buildHttpClient(Duration.ofSeconds(30));
	}
	
	public NAIAPI(String accessToken, Duration readTimeout) {
		if (!PERSISTENT_KEY_PATTERN.matcher(accessToken).matches()) {
			throw new IllegalArgumentException("Access token \'"+accessToken+"\' appears to be incorrect!");
		}
		this.accessToken = formatAccessToken(accessToken);
		
		client = buildHttpClient(readTimeout);
	}
	
	private String formatAccessToken(String rawToken) {
		String formattedToken = rawToken.trim();
		if (!formattedToken.startsWith("Bearer ")) {
			formattedToken = "Bearer " + formattedToken;
		}
		
		return formattedToken;
	}
	
	private OkHttpClient buildHttpClient(Duration readTimeout) {
		return new OkHttpClient.Builder()
			    .addNetworkInterceptor(sharedRateLimiter)
			    .readTimeout(readTimeout)
			    .build();
	}
	
	// === User Info Endpoints ===
	
	// --- GET Endpoints
	
	public UserInfo fetchUserInformation() throws IOException {
		return fetchFromNovelAI("user/information", GENERAL_API_ROOT, UserInfo.class);
	}
	
	public UserPriority fetchUserPriority() throws IOException {
		return fetchFromNovelAI("user/priority", GENERAL_API_ROOT, UserPriority.class);
	}
	
	public UserSubscription fetchUserSubscription() throws IOException {
		return fetchFromNovelAI("user/subscription", GENERAL_API_ROOT, UserSubscription.class);
	}
	
	public UserKeystore fetchUserKeystore() throws IOException {
		return fetchFromNovelAI("user/keystore", GENERAL_API_ROOT, UserKeystore.class);
	}
	
	public UserData fetchUserData() throws IOException {
		return fetchFromNovelAI("user/data", GENERAL_API_ROOT, UserData.class);
	}
	
	// === Generation Endpoints ===
	
	// --- GET Endpoints
	
	public AudioWrapper generateVoice(VoiceGenerationRequest requestInfo) throws IOException {
		return fetchFromNovelAI("ai/generate-voice", GENERAL_API_ROOT, requestInfo, AudioWrapper.class,
				body->new AudioWrapper(body.bytes(), requestInfo.getVersion().getReturnAudioFormat()));
	}

	// --- POST Endpoints
	
	public ImageSetWrapper generateImage(ImageGenerationRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/generate-image", IMAGE_API_ROOT, payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public ImageSetWrapper annotateImage(ImageAnnotateRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/annotate-image", GENERAL_API_ROOT, payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public ImageSetWrapper upscaleImage(ImageUpscaleRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/upscale", GENERAL_API_ROOT, payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public ImageSetWrapper augmentImageGeneric(ImageAugmentRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/augment-image", IMAGE_API_ROOT, payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public SingleImageWrapper augmentImageSingleResult(ImageAugmentRequestSingleResult payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/augment-image", IMAGE_API_ROOT, payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new SingleImageWrapper(resultBody);
	}
	
	public ImageSetWrapperRemoveBackground augmentImageRemoveBackground(ImageAugmentRemoveBackgroundRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/augment-image", IMAGE_API_ROOT, payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapperRemoveBackground(resultBody);
	}
	
	public V4VibeData encodeImageVibe(ImageVibeEncodeRequest payload) throws IOException {
		byte[] resultBody = postToNovelAI("ai/encode-vibe", IMAGE_API_ROOT, payload, byte[].class, ResponseBody::bytes);
		
		return new V4VibeData(payload.getInformationExtracted(), payload.getImage().generateSha256(), V4VibeData.VibeEncodingType.getEncodingTypeForModel(payload.getModel()), resultBody);
	}
	
	public TextGenerationResponse generateText(TextGenerationRequest payload) throws IOException {
		payload = payload.toBuilder().build();
		payload.getParameters().setGetHiddenStates(false);
		
		String resultBody = postToNovelAI("ai/generate", payload.getModel().getEndpoint(), payload, String.class, t -> {
			try { return t.string(); } catch (IOException e) { return e.getLocalizedMessage(); }
		});
		
		JsonObject rawResponse = gson.fromJson(resultBody, JsonObject.class);
		if (rawResponse.has("error")) {
			throw new IOException("Error from NAI: "+rawResponse.get("error").getAsString());
		}
		if (!rawResponse.has("output")) {
			throw new IOException("Missing \"output\" field in response JSON: "+resultBody);
		}
		String output = rawResponse.get("output").getAsString();
		
		TextGenerationResponse response = new TextGenerationResponse();
		TokenizedChunk outputChunk = new TokenizedChunk(payload.getModel().getTokenizerForModel(), "");
		if (!output.isEmpty()) {
			if (payload.getParameters().isUseString()) {
				outputChunk.setTextChunk(output);
			} else {
				outputChunk.setTokensFromBase64(output);
			}
		}
		response.setOutput(outputChunk);
		response.setLogprobs(gson.fromJson(rawResponse.get("logprobs"), LogProbStep[].class));
		return response;
	}
	
	public double[] fetchHiddenStates(TextGenerationRequest payload) throws IOException {
		if (!payload.getModel().isSupportsHiddenStates()) {
			throw new IllegalArgumentException("Model '"+payload.getModel()+"' does not support fetching hidden states!");
		}
		payload = payload.toBuilder().build();
		payload.getParameters().setGetHiddenStates(true);
		
		String resultBody = postToNovelAI("ai/generate", GENERAL_API_ROOT, payload, String.class, t -> {
			try { return t.string(); } catch (IOException e) { return e.getLocalizedMessage(); }
		});
		
		JsonObject rawResponse = gson.fromJson(resultBody, JsonObject.class);
		if (rawResponse.has("error")) {
			throw new IOException("Error from NAI: "+rawResponse.get("error").getAsString());
		}
		if (!rawResponse.has("output")) {
			throw new IOException("Missing \"output\" field in response JSON: "+resultBody);
		}
		if (!rawResponse.get("output").isJsonArray()) {
			throw new IOException("\"output\" field in response JSON isn't an array: "+resultBody);
		}
		JsonArray output = rawResponse.get("output").getAsJsonArray();
		
		return output.asList().stream().mapToDouble(JsonElement::getAsDouble).toArray();
	}
	
	// === Utils ===
	
	private <T> ResultParseFunction<T> deserializerFromJSON(Class<T> targetClass) {
		return body->gson.fromJson(body.charStream(), targetClass);
	}
	
	private BuilderAssemblyFunction appendEndpointToUrl(final String endpoint) {
		return urlBuilder->urlBuilder.addPathSegments(endpoint);
	}
	
	private <T> T fetchFromNovelAI(String endpoint, String host, Class<T> targetClass) throws IOException, JsonSyntaxException, JsonIOException {
		return fetchFromNovelAI(appendEndpointToUrl(endpoint), host, targetClass, deserializerFromJSON(targetClass));
	}

	private <T> T fetchFromNovelAI(String endpoint, String host, IQueryStringPayload queryStringPayload, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		return fetchFromNovelAI(
				appendEndpointToUrl(endpoint)
					.andThen(queryStringPayload::appendQueryParameters), 
				host,
				targetClass, 
				deserializer);
	}

	private <T> T fetchFromNovelAI(BuilderAssemblyFunction urlModifiers, String host, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		Request request = new Request.Builder()
				.url(urlModifiers.apply(
						new HttpUrl.Builder()
							.scheme("https")
							.host(host))
						.build())
				.addHeader(AUTH_HEADER, accessToken)
				.get()
				.build();

		return executeAndParseRequest(deserializer, request);
	}


	private <T> T postToNovelAI(String endpoint, String host, Object payload, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		return postToNovelAI(appendEndpointToUrl(endpoint), host, payload, targetClass, deserializer);
	}
	private <T> T postToNovelAI(BuilderAssemblyFunction urlModifiers, String host, Object payload, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		Request request = new Request.Builder()
				.url(urlModifiers.apply(
						new HttpUrl.Builder()
							.scheme("https")
							.host(host))
						.build())
				.addHeader(AUTH_HEADER, accessToken)
				.post(RequestBody.create(gson.toJson(payload), MEDIA_TYPE_JSON))
				.build();
		return executeAndParseRequest(deserializer, request);
	}

	private <T> T executeAndParseRequest(ResultParseFunction<T> deserializer, Request request) throws IOException, TooManyRequestsException {
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code()==429) {
					throw new TooManyRequestsException(response);
				} else {
					throw new IOException("Recieved " + response.code()+": "+response.body().string());
				}
			}
			
			return deserializer.apply(response.body());
		}
	}
}
