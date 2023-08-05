package com.magicalpipelines.model;

// The Product.java data class will be used to represent records in the products topic.
public class Product {

  private Long id;
  private String name;

  public Long getId() {
    return this.id;

  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "{" + " id'=" + getId() + "'" + ", name='" + getName() + "'" + "}";

  }
}
