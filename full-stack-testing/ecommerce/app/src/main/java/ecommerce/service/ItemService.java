package ecommerce.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import ecommerce.model.Item;

@Service
public class ItemService {

  // Using a list to simulate in-memory storage
  private final List<Item> items = Arrays.asList(
      new Item("984058981", "Green", "M"),
      new Item("984058982", "Blue", "L"),
      new Item("984058983", "Red", "S"));

  public List<Item> getAllItems() {
    return items;
  }

  public Item createItem(Item newItem) {
    items.add(newItem);
    return newItem;
  }
}
