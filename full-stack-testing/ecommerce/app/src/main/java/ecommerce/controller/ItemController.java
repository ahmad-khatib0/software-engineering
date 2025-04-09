package ecommerce.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.model.Item;

@RestController
public class ItemController {

  @GetMapping("/items")
  public List<Item> getItems() {
    return List.of(new Item("984058981", "Green", "M"));
  }
}
