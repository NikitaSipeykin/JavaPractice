package hw.java_core.completable_future;

public class ProductInfo {
  private String name;
  private double price;
  private String description;
  private double rating;

  public ProductInfo(String name, double price, String description, double rating) {
    this.name = name;
    this.price = price;
    this.description = description;
    this.rating = rating;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  @Override
  public String toString() {
    return "ProductInfo{" +
           "name='" + name + '\'' +
           ", price=" + price +
           ", description='" + description + '\'' +
           ", rating=" + rating +
           '}';
  }
}
