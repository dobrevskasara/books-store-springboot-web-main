package com.hendisantika.controller;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.hendisantika.entity.Books;
import com.hendisantika.service.framework.BooksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)

@WebMvcTest(
        controllers = BooksController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.hendisantika.config.SecurityConfig.class
        )
)



class BooksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BooksService booksService;

    @Test
    void test_getAllBooks_successful() throws Exception {
        Books book1 = new Books();
        book1.setId(1L);
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert Martin");

        Books book2 = new Books();
        book2.setId(2L);
        book2.setTitle("Effective Java");
        book2.setAuthor("Joshua Bloch");

        when(booksService.getAllBooks())
                .thenReturn(List.of(book1, book2));

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[1].author").value("Joshua Bloch"));
    }

    @Test
    void test_getBookById_successful() throws Exception {
        Books book = new Books();
        book.setId(1L);
        book.setTitle("Clean Code");

        when(booksService.findById(1L))
                .thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void test_getBookById_NotFound () throws Exception {
        when(booksService.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_addBook_successful() throws Exception {
        Books book = new Books();
        book.setId(1L);
        book.setTitle("New Book");
        book.setAuthor("Author");

        when(booksService.save(org.mockito.ArgumentMatchers.any(Books.class)))
                .thenReturn(book);

        String json = """
                {
                  "title": "New Book",
                  "author": "Author",
                  "description": "Test description",
                  "price": 20,
                  "stock": 5
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    void test_deleteBook_successful() throws Exception {
        Books book = new Books();
        book.setId(1L);

        when(booksService.findById(1L))
                .thenReturn(Optional.of(book));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Books with ID 1 is deleted"));
    }
    @Test
    void test_updateBooks_successful() throws Exception {
        Books existingBook = new Books();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("Old Author");

        Books updatedBook = new Books();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Updated Author");
        updatedBook.setDescription("Updated description");
        updatedBook.setPrice(25);
        updatedBook.setStock(10);

        when(booksService.findById(1L)).thenReturn(Optional.of(existingBook));
        when(booksService.save(org.mockito.ArgumentMatchers.any(Books.class)))
                .thenReturn(updatedBook);

        String json = """
            {
              "title": "Updated Title",
              "author": "Updated Author",
              "description": "Updated description",
              "price": 25,
              "stock": 10
            }
            """;

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author").value("Updated Author"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(25))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void test_updateBooks_bookNotFound() throws Exception {
        // Не постои книга со ID 99
        when(booksService.findById(99L)).thenReturn(Optional.empty());

        String json = """
            {
              "title": "Updated Title",
              "author": "Updated Author",
              "description": "Updated description",
              "price": 25,
              "stock": 10
            }
            """;

        mockMvc.perform(put("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
    //  POST со празен title → HTTP 400 Bad Request
    @Test
    void test_addBook_validationError_emptyTitle() throws Exception {
        String json = """
            {
              "title": "",
              "author": "Author",
              "description": "Test description",
              "price": 20,
              "stock": 5
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    //  PUT со празен author → HTTP 400 Bad Request
    @Test
    void test_updateBooks_validationError_emptyAuthor() throws Exception {
        Books existingBook = new Books();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("Old Author");

        when(booksService.findById(1L)).thenReturn(Optional.of(existingBook));

        String json = """
            {
              "title": "Updated Title",
              "author": "",
              "description": "Updated description",
              "price": 25,
              "stock": 10
            }
            """;

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    //  DELETE со ID кој не постои → HTTP 404
    @Test
    void test_deleteBooks_bookNotFound() throws Exception {
        when(booksService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    // GET со ID=0 → ID не постои во системот → BooksNotFoundException → HTTP 404 Not Found.

    @Test
    void test_getBookById_invalidId() throws Exception {
        mockMvc.perform(get("/api/books/0"))
                .andExpect(status().isNotFound());

    }

    // PUT со ID=0 → ID не постои во системот → BooksNotFoundException → HTTP 404 Not Found.
    @Test
    void test_updateBooks_invalidId() throws Exception {
        String json = """
            {
              "title": "Updated Title",
              "author": "Updated Author",
              "description": "Updated description",
              "price": 25,
              "stock": 10
            }
            """;

        mockMvc.perform(put("/api/books/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

    }


}
