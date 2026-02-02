package com.hendisantika.controller;

import com.hendisantika.entity.Books;
import com.hendisantika.service.framework.BooksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(
        controllers = BooksWebController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.hendisantika.config.SecurityConfig.class
        )
)
@AutoConfigureMockMvc(addFilters = false)

class BooksWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BooksService booksService;

    @Test
    void index_shouldReturnIndexViewWithBooks() throws Exception {
        Books book = new Books();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");

        when(booksService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void create_shouldReturnFormBooksView() throws Exception {
        mockMvc.perform(get("/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("formBooks"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void addBooks_shouldRedirectToIndexAfterSaving() throws Exception {
        Books book = new Books();
        book.setId(1L);
        book.setTitle("New Book");
        book.setAuthor("Author");

        when(booksService.save(any(Books.class))).thenReturn(book);

        mockMvc.perform(post("/create")
                        .param("title", "New Book")
                        .param("author", "Author")
                        .param("description", "Test description")
                        .param("price", "20")
                        .param("stock", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void detail_shouldReturnDetailBookViewWithBook() throws Exception {
        Books book = new Books();
        book.setId(1L);
        book.setTitle("Clean Code");

        when(booksService.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/detail/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detailBook"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void edit_shouldReturnFormBooksViewWithBook() throws Exception {
        Books book = new Books();
        book.setId(1L);
        book.setTitle("Clean Code");

        when(booksService.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("formBooks"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void delete_shouldRedirectToIndexAfterDeleting() throws Exception {
        doNothing().when(booksService).deleteById(1L);

        mockMvc.perform(get("/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(booksService, times(1)).deleteById(1L);
    }
}
