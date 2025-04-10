package ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {
  @JsonProperty("sku") // Ensures proper case in JSON
  private String SKU;
  private String color;
  private String size;

  // Default constructor needed for Jackson
  public Item() {
  }

  public Item(String SKU, String color, String size) {
    this.SKU = SKU;
    this.color = color;
    this.size = size;
  }

  // Getters and Setters
  public String getSKU() {
    return SKU;
  }

  public void setSKU(String SKU) {
    this.SKU = SKU;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }
}
