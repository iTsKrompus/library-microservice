package com.library.service;

import com.library.dto.BookDto;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateIsbnException;
import com.library.exception.NoCopiesAvailableException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return toDto(book);
    }

    public BookDto getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        return toDto(book);
    }

    public List<BookDto> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDto createBook(BookDto bookDto) {
        bookRepository.findByIsbn(bookDto.getIsbn()).ifPresent(b -> {
            throw new DuplicateIsbnException("ISBN already exists: " + bookDto.getIsbn());
        });
        Book book = toEntity(bookDto);
        book.setAvailableCopies(bookDto.getTotalCopies());
        return toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPublishedYear(bookDto.getPublishedYear());
        book.setTotalCopies(bookDto.getTotalCopies());
        return toDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public BookDto decreaseAvailableCopies(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        if (book.getAvailableCopies() <= 0) {
            throw new NoCopiesAvailableException("No copies available for book: " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto increaseAvailableCopies(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return toDto(bookRepository.save(book));
    }

    private BookDto toDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    private Book toEntity(BookDto dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .publishedYear(dto.getPublishedYear())
                .totalCopies(dto.getTotalCopies())
                .build();
    }
}
