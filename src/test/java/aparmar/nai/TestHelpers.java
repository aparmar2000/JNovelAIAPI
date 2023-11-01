package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.platform.commons.util.ReflectionUtils;

public class TestHelpers {
	public static <T> void autoTestDataAndToBuilderAnnotation(Class<T> targetClass, T testInstance, T differentTestInstance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Test equals, hashcode, and toString
		assertEquals(testInstance, testInstance);
		assertEquals(testInstance.hashCode(), testInstance.hashCode());
		assertFalse(testInstance.toString().isEmpty());
		assertEquals(testInstance.toString(), testInstance.toString());
		assertEquals(differentTestInstance, differentTestInstance);
		assertEquals(differentTestInstance.hashCode(), differentTestInstance.hashCode());
		assertFalse(differentTestInstance.toString().isEmpty());
		assertEquals(differentTestInstance.toString(), differentTestInstance.toString());
		
		assertNotEquals(testInstance, differentTestInstance);
		assertNotEquals(testInstance.hashCode(), differentTestInstance.hashCode());
		assertNotEquals(testInstance.toString(), differentTestInstance.toString());
		
		// Find toBuilder, if it exists, and test it
		Optional<Method> toBuilderMethodOptional = ReflectionUtils.findMethod(targetClass, "toBuilder", "");
		if (toBuilderMethodOptional.isPresent()) {
			Method toBuilderMethod = toBuilderMethodOptional.get();
			
			Object tempBuilder = toBuilderMethod.invoke(testInstance, new Object[0]);
			assertFalse(tempBuilder.toString().isEmpty());
			Optional<Method> buildMethodOptional = ReflectionUtils.findMethod(tempBuilder.getClass(), "build", "");
			assertTrue(buildMethodOptional.isPresent());
			Method buildMethod = buildMethodOptional.get();
			buildMethod.setAccessible(true);
			Object copyObject = buildMethod.invoke(tempBuilder, new Object[0]);
			assertEquals(testInstance, copyObject);
			
			tempBuilder = toBuilderMethod.invoke(differentTestInstance, new Object[0]);
			assertFalse(tempBuilder.toString().isEmpty());
			buildMethodOptional = ReflectionUtils.findMethod(tempBuilder.getClass(), "build", "");
			assertTrue(buildMethodOptional.isPresent());
			buildMethod = buildMethodOptional.get();
			buildMethod.setAccessible(true);
			copyObject = buildMethodOptional.get().invoke(tempBuilder, new Object[0]);
			assertEquals(differentTestInstance, copyObject);
		}
		
		// Find setters and getters
		List<Method> getterSetterMethods = 
				ReflectionUtils.findMethods(targetClass, m->m.getName().matches("^(?:is|get|set).+$"));
		HashMap<String, Method> getterMap = new HashMap<>();
		HashMap<String, Method> setterMap = new HashMap<>();
		for (Method method : getterSetterMethods) {
			String fieldName = method.getName().replaceFirst("^(?:is|get|set)", "");
			
			if (method.getReturnType().equals(Void.TYPE)) {
				setterMap.put(fieldName, method);
			} else {
				getterMap.put(fieldName, method);
			}
		}
		
		// "Test" setters and getters
		// Getter tests
		for (String fieldName : getterMap.keySet()) {
			Method getter = getterMap.get(fieldName);
			Method setter = setterMap.get(fieldName);
			if (setter == null) { continue; }// Don't test the getters of unchangeable fields
			
			assertEquals(0, getter.getParameterCount(),
					"Getter for field '"+fieldName+"' has a parameter!");
			arrayAwareObjectSimilarity(
					getter.invoke(testInstance, new Object[0]), 
					getter.invoke(differentTestInstance, new Object[0]), 
					false, 
					"Both test instances have equal values for field '"+fieldName+"'!");
		}
		// Setter tests
		for (String fieldName : getterMap.keySet()) {
			Method setter = setterMap.get(fieldName);
			Method getter = getterMap.get(fieldName);
			
			if (setter == null) { continue; }
			assertEquals(1, setter.getParameterCount(),
					"Setter for field '"+fieldName+"' has more than one parameter!");
			
			assertEquals(setter.getGenericParameterTypes()[0], getter.getGenericReturnType(),
					"Setter and getter for field '"+fieldName+"' have differing types!");
			setter.invoke(testInstance, getter.invoke(differentTestInstance, new Object[0]));
			arrayAwareObjectSimilarity(
					getter.invoke(testInstance, new Object[0]), 
					getter.invoke(differentTestInstance, new Object[0]), 
					true, 
					"Setter didn't correctly set value of field '"+fieldName+"'!");
		}
	}
	
	private static void arrayAwareObjectSimilarity(Object expected, Object actual, boolean equal, String message) {
		
		if ((expected == null || actual == null)
				|| (!expected.getClass().isArray() || !actual.getClass().isArray())) {
			if (equal) {
				assertEquals(expected, actual, message);
			} else {
				assertNotEquals(expected, actual, message);
			}
			return;
		}

		if (equal) {
			assertEquals(expected.getClass().getComponentType(), actual.getClass().getComponentType(), message);
		} else {
			if (!Objects.equals(expected.getClass().getComponentType(), actual.getClass().getComponentType())) { return; };
		}
		
		if (expected instanceof Object[]) {
			if (equal) {
				assertArrayEquals((Object[])expected, (Object[])actual, message);
			} else {
				assertFalse(Arrays.equals((Object[])expected, (Object[])actual), message);
			}
			return;
		}
		
		if (expected instanceof boolean[]) {
			if (equal) {
				assertArrayEquals((boolean[])expected, (boolean[])actual, message);
			} else {
				assertFalse(Arrays.equals((boolean[])expected, (boolean[])actual), message);
			}
			return;
		}
		
		if (expected instanceof byte[]) {
			if (equal) {
				assertArrayEquals((byte[])expected, (byte[])actual, message);
			} else {
				assertFalse(Arrays.equals((byte[])expected, (byte[])actual), message);
			}
			return;
		}
		
		if (expected instanceof short[]) {
			if (equal) {
				assertArrayEquals((short[])expected, (short[])actual, message);
			} else {
				assertFalse(Arrays.equals((short[])expected, (short[])actual), message);
			}
			return;
		}
		
		if (expected instanceof char[]) {
			if (equal) {
				assertArrayEquals((char[])expected, (char[])actual, message);
			} else {
				assertFalse(Arrays.equals((char[])expected, (char[])actual), message);
			}
			return;
		}
		
		if (expected instanceof int[]) {
			if (equal) {
				assertArrayEquals((int[])expected, (int[])actual, message);
			} else {
				assertFalse(Arrays.equals((int[])expected, (int[])actual), message);
			}
			return;
		}
		
		if (expected instanceof long[]) {
			if (equal) {
				assertArrayEquals((long[])expected, (long[])actual, message);
			} else {
				assertFalse(Arrays.equals((long[])expected, (long[])actual), message);
			}
			return;
		}
		
		if (expected instanceof float[]) {
			if (equal) {
				assertArrayEquals((float[])expected, (float[])actual, 0.000001f, message);
			} else {
				assertFalse(Arrays.equals((float[])expected, (float[])actual), message);
			}
			return;
		}
		
		if (expected instanceof double[]) {
			if (equal) {
				assertArrayEquals((double[])expected, (double[])actual, 0.000001, message);
			} else {
				assertFalse(Arrays.equals((double[])expected, (double[])actual), message);
			}
			return;
		}
	}
}
