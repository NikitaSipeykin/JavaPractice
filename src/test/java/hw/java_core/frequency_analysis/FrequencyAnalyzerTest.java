package hw.java_core.frequency_analysis;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyAnalyzerTest {

  @Test
  void shouldCountStringFrequencies() {
    String[] array = {"A", "B", "A", "C", "B", "A"};

    Map<String, Integer> result =
        FrequencyAnalyzer.frequencyAnalysis(array);

    assertEquals(3, result.get("A"));
    assertEquals(2, result.get("B"));
    assertEquals(1, result.get("C"));
    assertEquals(3, result.size());
  }

  @Test
  void shouldCountIntegerFrequencies() {
    Integer[] array = {5, 5, 5, 5};

    Map<Integer, Integer> result =
        FrequencyAnalyzer.frequencyAnalysis(array);

    assertEquals(4, result.get(5));
    assertEquals(1, result.size());
  }

  @Test
  void shouldCountDoubleFrequencies() {
    Double[] array = {1.5, 2.5, 1.5, 3.5, 2.5, 1.5};

    Map<Double, Integer> result =
        FrequencyAnalyzer.frequencyAnalysis(array);

    assertEquals(3, result.get(1.5));
    assertEquals(2, result.get(2.5));
    assertEquals(1, result.get(3.5));
    assertEquals(3, result.size());
  }

  @Test
  void shouldReturnEmptyMapForEmptyArray() {
    String[] array = {};

    Map<String, Integer> result =
        FrequencyAnalyzer.frequencyAnalysis(array);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldCountEveryElementOnce() {
    String[] array = {"A", "B", "C"};

    Map<String, Integer> result =
        FrequencyAnalyzer.frequencyAnalysis(array);

    assertEquals(1, result.get("A"));
    assertEquals(1, result.get("B"));
    assertEquals(1, result.get("C"));
    assertEquals(3, result.size());
  }

  @Test
  void shouldHandleNullElements() {
    String[] array = {"A", null, "A", null, null};

    Map<String, Integer> result =
        FrequencyAnalyzer.frequencyAnalysis(array);

    assertEquals(2, result.get("A"));
    assertEquals(3, result.get(null));
    assertEquals(2, result.size());
  }
}