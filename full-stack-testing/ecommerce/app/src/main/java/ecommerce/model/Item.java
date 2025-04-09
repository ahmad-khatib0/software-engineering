package ecommerce.model;

public class Item {
  private String SKU;
  private String Color;
  private String Size;

  public Item(String SKU, String Color, String Size) {
    this.SKU = SKU;
    this.Color = Color;
    this.Size = Size;
  }

  public String getSKU() {
    return SKU;
  }

  public String getColor() {
    return Color;
  }

  public String getSize() {
    return Size;
  }
}
