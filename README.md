[![](https://jitpack.io/v/aparmar2000/JNovelAIAPI.svg)](https://jitpack.io/#aparmar2000/JNovelAIAPI)
# JNovelAIAPI

This is a decently-functional Java library that facilitates usage of the NovelAI official API. It handles _most_ things for you automatically, such as rate-limiting (one request per second), parsing responses, tokenizing, etc.

This was originally created for my own use, but then I realized that other people might also find it useful. Feel free to make an Issue if you run into a bug or functionality limitation.

## Installing
### Maven
First add Jitpack as a repository, since JNovelAIAPI uses Jitpack to distribute dependencies:
```xml
<repositories>
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
</repositories>
```
Then, add JNovelAIAPI as a dependency:
```xml
<dependency>
    <groupId>com.github.aparmar2000</groupId>
    <artifactId>JNovelAIAPI</artifactId>
    <version>1.0.0</version>
</dependency>
```
### Gradle
Add the Jitpack repository:
```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
Then add the dependency:
```gradle
dependencies {
	        implementation 'com.github.aparmar2000:JNovelAIAPI:1.0.0'
	}
```

## Quickstart
To get started, simply create a new instance of `NAIAPI`:
```java
NAIAPI nai = new NAIAPI(PERSISTENT_API_KEY_GOES_HERE);
```
Then call any of the endpoint methods - for example, to generate an image:
```java
ImageGenerationRequest imgGenRequest = ImageGenerationRequest.builder()
      .input("portrait of a woman")
      .action(ImageGenAction.GENERATE)
      .model(ImageGenModel.ANIME_V2)
      .parameters(Image2ImageParameters.builder()
          .seed(1)
          .width(512)
          .height(512)
          .steps(28)
          .scale(10)
          .sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
          .undesiredContent(ImageGenerationRequest.ANIME_V2_LIGHT_UC)
          .build())
      .build();
ImageSetWrapper result = nai.generateImage(imgGenRequest);

result.writeImageToFile(0, new File("test_portrait.png"));
```
Or, generate some text like so:
```java
TextGenerationParameters presetGenerationParameters = TextParameterPresets.getPresetByExtendedName("Plotfish");
TextGenerationRequest textGenRequest = TextGenerationRequest.builder()
      .model(TextGenModel.KAYRA)
      .input("This is an API call!\n")
      .parameters(presetGenerationParameters.toBuilder()
          .useString(true)
          .build())
      .build();
TextGenerationResponse response = nai.generateText(textGenRequest);

System.out.println(response.getOutput().getTextChunk())
```

Additional examples can be found in [the integration tests](src/test/java/aparmar/nai).
