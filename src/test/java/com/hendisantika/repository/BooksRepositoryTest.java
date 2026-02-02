package com.hendisantika.repository;

import com.hendisantika.entity.Books;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class BooksRepositoryTest {

    @Autowired
    private BooksRepository booksRepository;

    @Test
    void test_findByAuthor_Found() {

        Books book = new Books();
        book.setTitle("Clean Architecture");
        book.setAuthor("Robert Martin");
        book.setPrice(30);
        book.setStock(10);
        book.setDescription("Architecture best practices");

        booksRepository.save(book);


        Optional<Books> result = booksRepository.findByAuthor("Robert Martin");


        assertTrue(result.isPresent(), "Book should be found");
        assertEquals("Clean Architecture", result.get().getTitle());
    }
    @Test
    void test_findByAuthor_NotFound() {
        Optional<Books> result = booksRepository.findByAuthor("Non Existing Author");

        assertTrue(result.isEmpty());
    }
}
