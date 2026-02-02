//package com.hendisantika.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*; // или jakarta.persistence.* ако си со Spring Boot 3
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "books") // Ова гарантира дека табелата ќе се вика "books"
//public class Books {
//  @Id
////  @GeneratedValue(strategy = GenerationType.AUTO)
//  @GeneratedValue(strategy = GenerationType.AUTO)
//  private Long id;
//
//  @NotEmpty(message = "Title is required")
//  @Column(nullable = false, name = "title")
//  private String title;
//
//  @NotEmpty(message = "Author is required")
//  @Column(nullable = false, name = "author")
//  private String author;
//
//  @NotEmpty(message = "Description name is required")
//  @Column(nullable = false, name = "description")
//  private String description;
//
//  @NotNull(message = "Price Number is required")
//  @Column(nullable = false, name = "price")
//  private int price;
//
//  @NotNull(message = "Stock is required")
//  @Column(nullable = false, name = "stock")
//  private int stock;
//
////    // 2. GETTERS (За читање на податоци)
////    public Long getId() { return id; }
////    public String getTitle() { return title; }
////    public String getAuthor() { return author; }
////    public String getDescription() { return description; }
////    public int getPrice() { return price; }
////    public int getStock() { return stock; }
////
////    // 3. SETTERS (За менување на податоци)
////    public void setId(Long id) { this.id = id; }
////    public void setTitle(String title) { this.title = title; }
////    public void setAuthor(String author) { this.author = author; }
////    public void setDescription(String description) { this.description = description; }
////    public void setPrice(int price) { this.price = price; }
////    public void setStock(int stock) { this.stock = stock; }
//}
package com.hendisantika.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Title is required")
    @Column(nullable = false, name = "title")
    private String title;

    @NotEmpty(message = "Author is required")
    @Column(nullable = false, name = "author")
    private String author;

    @NotEmpty(message = "Description name is required")
    @Column(nullable = false, name = "description")
    private String description;

    @NotNull(message = "Price Number is required")
    @Column(nullable = false, name = "price")
    private int price;

    @NotNull(message = "Stock is required")
    @Column(nullable = false, name = "stock")
    private int stock;
}