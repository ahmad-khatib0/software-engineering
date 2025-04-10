package ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.model.Item;
import ecommerce.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

  private final ItemService itemService;

  @Autowired
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping
  public List<Item> getAllItems() {
    return itemService.getAllItems();
  }

  @PostMapping
  public ResponseEntity<Item> createItem(@RequestBody Item newItem) {
    Item createdItem = itemService.createItem(newItem);
    return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
  }
}
