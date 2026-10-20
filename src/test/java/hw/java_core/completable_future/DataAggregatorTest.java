package hw.java_core.completable_future;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataAggregatorTest {

  private static class TestDataAggregator extends DataAggregator {

    private boolean priceFails;
    private boolean descriptionFails;
    private boolean ratingFails;
    @Override
    public double fetchPrice(String productName) {
      if (priceFails) {
        throw new RuntimeException("Price service error");
      }
      return 546.87;
    }
    @Override
    public String fetchDescription(String productName) {
      if (descriptionFails) {
        throw new RuntimeException("Description service error");
      }
      return "Markus";
    }
    @Override
    public double fetchRating(String productName) {
      if (ratingFails) {
        throw new RuntimeException("Rating service error");
      }
      return 0.89;
    }

  }

  @Test
  void shouldAggregateProductInfoWhenAllServicesSucceed() {
    DataAggregator aggregator = new TestDataAggregator();

    ProductInfo result = aggregator.aggregateProductInfo("Ноутбук");
    assertNotNull(result);
    assertEquals("Ноутбук", result.getName());
    assertEquals(546.87, result.getPrice(), 0.001);
    assertEquals("Markus", result.getDescription());
    assertEquals(0.89, result.getRating(), 0.001);

  }

  @Test
  void shouldUseFallbackPriceWhenPriceServiceFails() {
    TestDataAggregator aggregator = new TestDataAggregator();
    aggregator.priceFails = true;

    ProductInfo result = aggregator.aggregateProductInfo("Ноутбук");
    assertEquals(0.0, result.getPrice(), 0.001);
    assertEquals("Markus", result.getDescription());
    assertEquals(0.89, result.getRating(), 0.001);

  }

  @Test
  void shouldUseFallbackDescriptionWhenDescriptionServiceFails() {
    TestDataAggregator aggregator = new TestDataAggregator();
    aggregator.descriptionFails = true;

    ProductInfo result = aggregator.aggregateProductInfo("Ноутбук");
    assertEquals(546.87, result.getPrice(), 0.001);
    assertEquals("no data", result.getDescription());
    assertEquals(0.89, result.getRating(), 0.001);

  }

  @Test
  void shouldUseFallbackRatingWhenRatingServiceFails() {
    TestDataAggregator aggregator = new TestDataAggregator();
    aggregator.ratingFails = true;

    ProductInfo result = aggregator.aggregateProductInfo("Ноутбук");
    assertEquals(546.87, result.getPrice(), 0.001);
    assertEquals("Markus", result.getDescription());
    assertEquals(0.0, result.getRating(), 0.001);

  }

  @Test
  void shouldUseFallbackValuesWhenAllServicesFail() {
    TestDataAggregator aggregator = new TestDataAggregator();
    aggregator.priceFails = true;
    aggregator.descriptionFails = true;
    aggregator.ratingFails = true;

    ProductInfo result = aggregator.aggregateProductInfo("Ноутбук");
    assertNotNull(result);
    assertEquals("Ноутбук", result.getName());
    assertEquals(0.0, result.getPrice(), 0.001);
    assertEquals("no data", result.getDescription());
    assertEquals(0.0, result.getRating(), 0.001);

  }
}