package hw.java_core.completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class DataAggregator {
  public ProductInfo aggregateProductInfo(String productName){
    CompletableFuture<Double> price = CompletableFuture
        .supplyAsync(() -> fetchPrice(productName))
        .exceptionally(throwable -> 0.0);

    CompletableFuture<PriceAndDescription> priceAndDescription = CompletableFuture
        .supplyAsync(() -> fetchDescription(productName))
        .exceptionally(throwable -> "no data")
        .thenCombine(price, (firstResult, secondResult) -> {
          return new PriceAndDescription(secondResult, firstResult);
        });


    CompletableFuture<ProductInfo> productInfoCompletableFuture = CompletableFuture
        .supplyAsync(() -> fetchRating(productName))
        .exceptionally(throwable -> 0.0)
        .thenCombine(priceAndDescription, (firstResult, secondResult) -> {
          return new ProductInfo(productName, secondResult.price(), secondResult.description(), firstResult);
        });

    return productInfoCompletableFuture.join();
  }

  public double fetchPrice(String productName){
    int seconds = ThreadLocalRandom.current().nextInt(1000, 3001);

    try {
      Thread.sleep(seconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (ThreadLocalRandom.current().nextDouble() < 0.2) {
      throw new RuntimeException("Ошибка сервиса");
    }

    return 546.87;
  }

  public String fetchDescription(String productName){
    int seconds = ThreadLocalRandom.current().nextInt(1000, 3001);

    try {
      Thread.sleep(seconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (ThreadLocalRandom.current().nextDouble() < 0.2) {
      throw new RuntimeException("Ошибка сервиса");
    }

    return "Markus";
  }

  public double fetchRating(String productName){
    int seconds = ThreadLocalRandom.current().nextInt(1000, 3001);

    try {
      Thread.sleep(seconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (ThreadLocalRandom.current().nextDouble() < 0.2) {
      throw new RuntimeException("Ошибка сервиса");
    }

    return 0.89;
  }

  private record PriceAndDescription(
      double price,
      String description
  ) {}
}
