package aparmar.nai;

import static aparmar.nai.utils.HelperConstants.API_ROOT;
import static aparmar.nai.utils.HelperConstants.AUTH_HEADER;
import static aparmar.nai.utils.HelperConstants.MEDIA_TYPE_AUDIO;
import static aparmar.nai.utils.HelperConstants.MEDIA_TYPE_JSON;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.ArgumentMatcher;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import aparmar.nai.data.request.IQueryStringPayload;
import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.TextGenerationParameters;
import aparmar.nai.data.request.TextGenerationRequest;
import aparmar.nai.data.request.VoiceGenerationRequest;
import aparmar.nai.data.request.VoiceGenerationRequest.PresetV2Voice;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.data.request.imagen.ImageParameters;
import aparmar.nai.data.response.AudioWrapper;
import aparmar.nai.data.response.TextGenerationResponse;
import aparmar.nai.data.response.TooManyRequestsException;
import aparmar.nai.data.response.UserData;
import aparmar.nai.data.response.UserInfo;
import aparmar.nai.data.response.UserKeystore;
import aparmar.nai.data.response.UserPriority;
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.data.response.UserSubscription.SubscriptionTier;
import aparmar.nai.utils.TextParameterPresets;
import aparmar.nai.utils.tokenization.TokenizedChunk;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

class UnitTestNAIAPI {
	private Gson gson = new Gson();

	private NAIAPI apiInstance;

	private OkHttpClient mockHttpClient;
	
	private static final String MOCK_API_KEY = "mockapikey";

	@BeforeEach
	public void setUp() throws Exception {
		mockHttpClient = mock(OkHttpClient.class);
		apiInstance = new NAIAPI(MOCK_API_KEY);

		Field clientField = ReflectionUtils.findFields(NAIAPI.class, f -> f.getType() == OkHttpClient.class,
				ReflectionUtils.HierarchyTraversalMode.TOP_DOWN).get(0);
		clientField.setAccessible(true);
		clientField.set(apiInstance, mockHttpClient);
	}
	

	private <T> void mockErrorResponse(int statusCode, String message) throws IOException {
		Call mockCall = mock(Call.class);
		Request testRequest = new Request.Builder()
                .url("https://nowhere.null")
                .build();
		Response testResponse = new Response.Builder()
				.request(testRequest)
                .protocol(Protocol.HTTP_2)
                .code(statusCode)
                .message("")
                .body(ResponseBody.create(message, MEDIA_TYPE_JSON))
                .build();
		when(mockHttpClient.newCall(any())).thenReturn(mockCall);
		when(mockCall.execute()).thenReturn(testResponse);
	}

	private <T> void mockResponseJson(T bodyObject, Class<T> bodyObjectClass) throws IOException {
		Call mockCall = mock(Call.class);
		Request testRequest = new Request.Builder()
                .url("https://nowhere.null")
                .build();
		Response testResponse = new Response.Builder()
				.request(testRequest)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("")
                .body(ResponseBody.create(gson.toJson(bodyObject, bodyObjectClass), MEDIA_TYPE_JSON))
                .build();
		when(mockHttpClient.newCall(any())).thenReturn(mockCall);
		when(mockCall.execute()).thenReturn(testResponse);
	}

	private void mockResponseAudio(byte[] body) throws IOException {
		Call mockCall = mock(Call.class);
		Request testRequest = new Request.Builder()
                .url("https://nowhere.null")
                .build();
		Response testResponse = new Response.Builder()
				.request(testRequest)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("")
                .body(ResponseBody.create(body, MEDIA_TYPE_AUDIO))
                .build();
		when(mockHttpClient.newCall(any())).thenReturn(mockCall);
		when(mockCall.execute()).thenReturn(testResponse);
	}
	
	private ArgumentMatcher<Request> requestMatcher(String endpoint) {
		return req -> {
			String authHeader = req.header(AUTH_HEADER);
			if (!("Bearer "+MOCK_API_KEY).equals(authHeader)) {
				return false;
			}
			if (!req.isHttps()) {return false;}
			if (!API_ROOT.equals(req.url().host())) {return false;}
			return ("/"+endpoint).equals(req.url().encodedPath());
		};
	}
	
	private ArgumentMatcher<Request> requestQueryMatcher(String endpoint, IQueryStringPayload payload) {
		return req -> {
			if (!requestMatcher(endpoint).matches(req)) { return false;}
			
			Map<String, ? extends Object> expectedQueryData = payload.getParameterValues();
			HashSet<String> matchedKeys = new HashSet<>();
			for (int i = 0; i < req.url().querySize(); i++) {
				String key = req.url().queryParameterName(i);
				String value = req.url().queryParameterValue(i);
				if (!expectedQueryData.containsKey(key) 
						|| !Objects.equals(value,expectedQueryData.get(key).toString())
						|| matchedKeys.contains(key)) {
					return false;
				}
				matchedKeys.add(key);
			}
			return matchedKeys.size() == expectedQueryData.size();
		};
	}
	
	private <T> ArgumentMatcher<Request> requestPayloadMatcher(String endpoint, T payload, Class<T> payloadClazz) {
		return req -> {
			if (!requestMatcher(endpoint).matches(req)) {
				throw new AssertionError("endpoint mismatch");
			}
			
			try (Buffer sink = new okio.Buffer()) {
				req.body().writeTo(sink);
				
				String requestPayloadString = sink.readString(Charset.defaultCharset());
				boolean matches = Objects.equals(payload, gson.fromJson(requestPayloadString, payloadClazz));
				if (!matches) {
					throw new AssertionError(payload+"\n\n"+gson.fromJson(requestPayloadString, payloadClazz));
				}
				return matches;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		};
	}

	@Test
	void testTooManyRequestsResponse() throws IOException {
		mockErrorResponse(429, "");
		assertThrows(TooManyRequestsException.class, apiInstance::fetchUserInformation);
	}
	
	@Test
	void testGenericErrorResponse() throws IOException {
		mockErrorResponse(500, "");
		assertThrows(IOException.class, apiInstance::fetchUserInformation);
	}

	@Test
	void testFetchUserInformation() throws IOException {
		UserInfo expectedUserInfo = new UserInfo(false, true, false, 69, 0);
		mockResponseJson(expectedUserInfo, UserInfo.class);

		UserInfo actualUserInfo = apiInstance.fetchUserInformation();
		verify(mockHttpClient).newCall(argThat(requestMatcher("user/information")));
		assertEquals(expectedUserInfo, actualUserInfo);
	}

	@Test
	void testFetchUserPriority() throws IOException {
		UserPriority expectedUserPriority = new UserPriority(69, 888, 12);
		mockResponseJson(expectedUserPriority, UserPriority.class);

		UserPriority actualUserPriority = apiInstance.fetchUserPriority();
		verify(mockHttpClient).newCall(argThat(requestMatcher("user/priority")));
		assertEquals(expectedUserPriority, actualUserPriority);
	}

	@Test
	void testFetchUserSubscription() throws IOException {
		UserSubscription.SubscriptionPerks expectedSubPerks = 
				new UserSubscription.SubscriptionPerks(69, 999, 8192, true, true, true, true, null, 888);
		UserSubscription.SubscriptionTrainingSteps expectedSubSteps = 
				new UserSubscription.SubscriptionTrainingSteps(888, 999);
		UserSubscription expectedUserSub = 
				new UserSubscription(SubscriptionTier.SCROLL, true, 9999, expectedSubPerks, JsonNull.INSTANCE, expectedSubSteps, 0);
		mockResponseJson(expectedUserSub, UserSubscription.class);

		UserSubscription actualUserSub = apiInstance.fetchUserSubscription();
		verify(mockHttpClient).newCall(argThat(requestMatcher("user/subscription")));
		assertEquals(expectedUserSub, actualUserSub);
	}

	@Test
	void testFetchUserKeystore() throws IOException {
		UserKeystore expectedUserKeystore = new UserKeystore("keysss");
		mockResponseJson(expectedUserKeystore, UserKeystore.class);

		UserKeystore actualUserKeystore = apiInstance.fetchUserKeystore();
		verify(mockHttpClient).newCall(argThat(requestMatcher("user/keystore")));
		assertEquals(expectedUserKeystore, actualUserKeystore);
	}

	@Test
	void testFetchUserData() throws IOException {
		UserPriority expectedUserPriority = new UserPriority(69, 888, 12);

		UserSubscription.SubscriptionPerks expectedSubPerks = 
				new UserSubscription.SubscriptionPerks(69, 999, 8192, true, true, true, true, null, 888);
		UserSubscription.SubscriptionTrainingSteps expectedSubSteps = 
				new UserSubscription.SubscriptionTrainingSteps(888, 999);
		UserSubscription expectedUserSub = 
				new UserSubscription(SubscriptionTier.SCROLL, true, 9999, expectedSubPerks, JsonNull.INSTANCE, expectedSubSteps, 1);

		UserKeystore expectedUserKeystore = new UserKeystore("keysss");

		UserInfo expectedUserInfo = new UserInfo(false, true, false, 69, 0);
		
		UserData expectedUserData = 
				new UserData(expectedUserPriority, expectedUserSub, expectedUserKeystore, "Settings", expectedUserInfo);
		mockResponseJson(expectedUserData, UserData.class);

		UserData actualUserData = apiInstance.fetchUserData();
		verify(mockHttpClient).newCall(argThat(requestMatcher("user/data")));
		assertEquals(expectedUserData, actualUserData);
	}

	@Test
	void testGenerateVoice() throws IOException {
		Random rand = new Random();
		
		byte[] expectedBytes = new byte[10];
		rand.nextBytes(expectedBytes);
		mockResponseAudio(expectedBytes);
		
		VoiceGenerationRequest testGenerationRequest = 
				VoiceGenerationRequest.presetV2VoiceRequest("test", PresetV2Voice.AINI);
		AudioWrapper actualAudioResponse = apiInstance.generateVoice(testGenerationRequest);
		verify(mockHttpClient).newCall(argThat(requestQueryMatcher("ai/generate-voice", testGenerationRequest)));
		assertArrayEquals(expectedBytes, actualAudioResponse.getBytes());
	}

	@Disabled
	@Test
	void testGenerateImage() throws IOException {
		fail("Not yet implemented"); // TODO
		
		ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
				.input("yiff")
				.model(ImageGenModel.FURRY)
				.parameters(new ImageParameters(
						1,
						512,512,
						28,1,
						ImageParameters.ImageGenSampler.K_EULER_ANCESTRAL,
						true, false, false, 
						true, ImageGenerationRequest.QualityTagsLocation.DEFAULT, 
						0, ImageGenerationRequest.FURRY_LOW_QUALITY_UC, 0,
						1))
				.build();
		apiInstance.generateImage(testGenerationRequest);
		verify(mockHttpClient).newCall(argThat(requestMatcher("ai/generate-image")));
	}

	@Test
	void testGenerateText() throws IOException {
		String testPresetName = TextParameterPresets.getAssociatedPresets(TextGenModel.CLIO)[0];
		TextGenerationParameters testPreset = TextParameterPresets.getPresetByExtendedName(testPresetName);
		TextGenerationRequest testTextRequest = TextGenerationRequest.builder()
				.model(TextGenModel.CLIO)
				.input("This is an API call!\n")
				.parameters(testPreset.toBuilder()
						.useString(true)
						.getHiddenStates(true)
						.build())
				.build();
		
		TextGenerationResponse mockResponse = new TextGenerationResponse();
		mockResponse.setOutput(new TokenizedChunk(TextGenModel.CLIO, "test"));
		JsonObject mockResponseJson = new JsonObject();
		mockResponseJson.addProperty("output", mockResponse.getOutput().getTextChunk());
		mockResponseJson(mockResponseJson, JsonObject.class);
		
		TextGenerationResponse actualResponse = apiInstance.generateText(testTextRequest);
		testTextRequest.getParameters().setGetHiddenStates(false);
		verify(mockHttpClient).newCall(argThat(requestPayloadMatcher("ai/generate", testTextRequest, TextGenerationRequest.class)));
		assertEquals(mockResponse, actualResponse);
	}

	@Test
	void testFetchHiddenStates() throws IOException {
		Random rand = new Random();
		
		String testPresetName = TextParameterPresets.getAssociatedPresets(TextGenModel.EUTERPE)[0];
		TextGenerationParameters testPreset = TextParameterPresets.getPresetByExtendedName(testPresetName);
		TextGenerationRequest testTextRequest = TextGenerationRequest.builder()
				.model(TextGenModel.EUTERPE)
				.input("This is an API call!\n")
				.parameters(testPreset.toBuilder()
						.useString(true)
						.getHiddenStates(false)
						.build())
				.build();
		
		double[] mockResponse = rand.doubles(15).toArray();
		JsonObject mockResponseJson = new JsonObject();
		mockResponseJson.add("output", gson.toJsonTree(mockResponse));
		mockResponseJson(mockResponseJson, JsonObject.class);
		
		double[] actualResponse = apiInstance.fetchHiddenStates(testTextRequest);
		testTextRequest.getParameters().setGetHiddenStates(true);
		verify(mockHttpClient).newCall(argThat(requestPayloadMatcher("ai/generate", testTextRequest, TextGenerationRequest.class)));
		assertArrayEquals(mockResponse, actualResponse);
	}

}
