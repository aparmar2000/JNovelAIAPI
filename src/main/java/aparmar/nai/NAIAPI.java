package aparmar.nai;

import static aparmar.nai.utils.HelperConstants.API_ROOT;
import static aparmar.nai.utils.HelperConstants.AUTH_HEADER;
import static aparmar.nai.utils.HelperConstants.MEDIA_TYPE_JSON;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.IQueryStringPayload;
import aparmar.nai.data.request.ImageAnnotateRequest;
import aparmar.nai.data.request.ImageUpscaleRequest;
import aparmar.nai.data.request.ImageUpscaleRequest.UpscaleFactor;
import aparmar.nai.data.request.TextGenerationRequest;
import aparmar.nai.data.request.VoiceGenerationRequest;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.response.AudioWrapper;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.data.response.TextGenerationResponse;
import aparmar.nai.data.response.TextGenerationResponse.LogProb;
import aparmar.nai.data.response.TextGenerationResponse.LogProbStep;
import aparmar.nai.data.response.TooManyRequestsException;
import aparmar.nai.data.response.UserData;
import aparmar.nai.data.response.UserInfo;
import aparmar.nai.data.response.UserKeystore;
import aparmar.nai.data.response.UserPriority;
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.utils.BuilderAssemblyFunction;
import aparmar.nai.utils.RateLimitInterceptor;
import aparmar.nai.utils.ResultParseFunction;
import aparmar.nai.utils.ZipArchiveWrapper;
import aparmar.nai.utils.ZipParseFunction;
import aparmar.nai.utils.tokenization.INaiTokenizer;
import aparmar.nai.utils.tokenization.TokenizedChunk;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NAIAPI {	
	private final OkHttpClient client;
	private final Gson gson;
	
	private final String accessToken;
	
	public NAIAPI(String accessToken) {
		accessToken = accessToken.trim();
		if (!accessToken.startsWith("Bearer ")) {
			accessToken = "Bearer " + accessToken;
		}
		this.accessToken = accessToken;
		
		client = new OkHttpClient.Builder()
			    .addNetworkInterceptor(new RateLimitInterceptor(1))
			    .build();
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LogProb.class, new LogProb());
		gsonBuilder.registerTypeAdapter(Base64Image.class, new Base64Image());
		gsonBuilder.registerTypeAdapter(UpscaleFactor.class, UpscaleFactor.TWO);
		gsonBuilder.registerTypeAdapter(ImageUpscaleRequest.class, new ImageUpscaleRequest());
		gsonBuilder.registerTypeAdapter(ImageAnnotateRequest.class, ImageAnnotateRequest.SERIALIZER_INSTANCE);
		gson = gsonBuilder.create();
	}
	
	// === User Info Endpoints ===
	
	// --- GET Endpoints
	
	public UserInfo fetchUserInformation() throws IOException {
		return fetchFromNovelAI("user/information", UserInfo.class);
	}
	
	public UserPriority fetchUserPriority() throws IOException {
		return fetchFromNovelAI("user/priority", UserPriority.class);
	}
	
	public UserSubscription fetchUserSubscription() throws IOException {
		return fetchFromNovelAI("user/subscription", UserSubscription.class);
	}
	
	public UserKeystore fetchUserKeystore() throws IOException {
		return fetchFromNovelAI("user/keystore", UserKeystore.class);
	}
	
	public UserData fetchUserData() throws IOException {
		return fetchFromNovelAI("user/data", UserData.class);
	}
	
	// === Generation Endpoints ===
	
	// --- GET Endpoints
	
	public AudioWrapper generateVoice(VoiceGenerationRequest requestInfo) throws IOException {
		return fetchFromNovelAI("ai/generate-voice", requestInfo, AudioWrapper.class,
				body->new AudioWrapper(body.bytes(), requestInfo.getVersion().getReturnAudioFormat()));
	}

	// --- POST Endpoints
	
	public ImageSetWrapper generateImage(ImageGenerationRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/generate-image", payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public ImageSetWrapper annotateImage(ImageAnnotateRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/annotate-image", payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public ImageSetWrapper upscaleImage(ImageUpscaleRequest payload) throws IOException {
		ZipArchiveWrapper resultBody = postToNovelAI("ai/upscale", payload, ZipArchiveWrapper.class, new ZipParseFunction()::apply);
		
		return new ImageSetWrapper(resultBody);
	}
	
	public TextGenerationResponse generateText(TextGenerationRequest payload) throws IOException {
		String resultBody = postToNovelAI("ai/generate", payload, String.class, t -> {
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
				outputChunk.setTokens(INaiTokenizer.base64ToTokens(output));
			}
		}
		response.setOutput(outputChunk);
		response.setLogprobs(gson.fromJson(rawResponse.get("logprobs"), LogProbStep[].class));
		return response;
	}
	
	// === Utils ===
	
	private <T> ResultParseFunction<T> deserializerFromJSON(Class<T> targetClass) {
		return body->gson.fromJson(body.charStream(), targetClass);
	}
	
	private BuilderAssemblyFunction appendEndpointToUrl(final String endpoint) {
		return urlBuilder->urlBuilder.addPathSegments(endpoint);
	}
	
	private <T> T fetchFromNovelAI(String endpoint, Class<T> targetClass) throws IOException, JsonSyntaxException, JsonIOException {
		return fetchFromNovelAI(appendEndpointToUrl(endpoint), targetClass, deserializerFromJSON(targetClass));
	}

	private <T> T fetchFromNovelAI(String endpoint, IQueryStringPayload queryStringPayload, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		return fetchFromNovelAI(
				appendEndpointToUrl(endpoint)
					.andThen(queryStringPayload::appendQueryParameters), 
				targetClass, 
				deserializer);
	}

	private <T> T fetchFromNovelAI(BuilderAssemblyFunction urlModifiers, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		Request request = new Request.Builder()
				.url(urlModifiers.apply(
						new HttpUrl.Builder()
							.scheme("https")
							.host(API_ROOT))
						.build())
				.addHeader(AUTH_HEADER, accessToken)
				.get()
				.build();

		return executeAndParseRequest(deserializer, request);
	}


	private <T> T postToNovelAI(String endpoint, Object payload, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		return postToNovelAI(appendEndpointToUrl(endpoint), payload, targetClass, deserializer);
	}
	private <T> T postToNovelAI(BuilderAssemblyFunction urlModifiers, Object payload, Class<T> targetClass, ResultParseFunction<T> deserializer) throws IOException, JsonSyntaxException, JsonIOException {
		Request request = new Request.Builder()
				.url(urlModifiers.apply(
						new HttpUrl.Builder()
							.scheme("https")
							.host(API_ROOT))
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
