package hw.java_core.frequency_analysis;

import java.util.HashMap;
import java.util.Map;

public class FrequencyAnalyzer {
  public static <T> Map<T, Integer> frequencyAnalysis(T[] array){
    Map<T, Integer> map = new HashMap<>();

    for (T element: array) {
      map.merge(element, 1, Integer::sum);
    }
    return map;
  }
}
