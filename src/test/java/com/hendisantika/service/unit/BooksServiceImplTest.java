package com.hendisantika.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hendisantika.entity.Books;
import com.hendisantika.repository.BooksRepository;

import com.hendisantika.service.implementation.BooksServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BooksServiceImplTest {

    @Mock
    private BooksRepository booksRepository;

    @InjectMocks
    private BooksServiceImpl booksService;

    private Books book1;
    private Books book2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book1 = new Books();
        book1.setId(1L);
        book1.setAuthor("Author1");
        book1.setTitle("Title1");

        book2 = new Books();
        book2.setId(2L);
        book2.setAuthor("Author2");
        book2.setTitle("Title2");
    }

    @Test
    void test_GetAllBooks_Found() {
        when(booksRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Books> books = booksService.getAllBooks();

        assertEquals(2, books.size());
        verify(booksRepository, times(1)).findAll();
    }

    @Test
    void test_GetAllBooks_Empty() {
        when(booksRepository.findAll()).thenReturn(List.of());
        List<Books> books = booksService.getAllBooks();
        assertTrue(books.isEmpty());
        verify(booksRepository, times(1)).findAll();
    }


    @Test
    void test_findById_Found() {
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Books> found = booksService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("Author1", found.get().getAuthor());
        verify(booksRepository, times(1)).findById(1L);
    }

    @Test
    void test_findById_NotFound() {
        when(booksRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Books> found = booksService.findById(3L);

        assertFalse(found.isPresent());
        verify(booksRepository, times(1)).findById(3L);
    }

    @Test
    void test_findByAuthor_Found() {
        when(booksRepository.findByAuthor("Author1")).thenReturn(Optional.of(book1));

        Optional<Books> found = booksService.findByAuthor("Author1");

        assertTrue(found.isPresent());
        assertEquals("Title1", found.get().getTitle());
        verify(booksRepository, times(1)).findByAuthor("Author1");
    }
    @Test
    void test_findByAuthor_NotFound() {
        when(booksRepository.findByAuthor("X")).thenReturn(Optional.empty());
        Optional<Books> found = booksService.findByAuthor("X");
        assertFalse(found.isPresent());
    }

    @Test
    void test_save() {
        when(booksRepository.save(book1)).thenReturn(book1);

        Books saved = booksService.save(book1);

        assertNotNull(saved);
        assertEquals("Author1", saved.getAuthor());
        verify(booksRepository, times(1)).save(book1);
    }
    @Test
    void test_save_Null() {
        when(booksRepository.save(null)).thenReturn(null);
        Books saved = booksService.save(null);
        assertNull(saved);
        verify(booksRepository).save(null);
    }

    @Test
    void test_deleteById() {
        doNothing().when(booksRepository).deleteById(1L);

        booksService.deleteById(1L);

        verify(booksRepository, times(1)).deleteById(1L);
    }

}
