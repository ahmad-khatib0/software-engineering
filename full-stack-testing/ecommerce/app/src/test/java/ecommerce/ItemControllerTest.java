package ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ecommerce.controller.ItemController;
import ecommerce.model.Item;
import ecommerce.service.ItemService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ItemService itemService;

  // ... existing tests ...

  @Test
  public void createItem_ShouldReturnCreatedItem() throws Exception {
    Item newItem = new Item("123456789", "Black", "XL");
    when(itemService.createItem(any(Item.class))).thenReturn(newItem);

    mockMvc.perform(post("/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"sku\":\"123456789\",\"color\":\"Black\",\"size\":\"XL\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.sku").value("123456789"))
        .andExpect(jsonPath("$.color").value("Black"))
        .andExpect(jsonPath("$.size").value("XL"));
  }
}
